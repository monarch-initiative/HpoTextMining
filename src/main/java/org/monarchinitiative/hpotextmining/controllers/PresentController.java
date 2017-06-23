package org.monarchinitiative.hpotextmining.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.monarchinitiative.hpotextmining.application.DialogController;
import org.monarchinitiative.hpotextmining.application.FXMLDialog;
import org.monarchinitiative.hpotextmining.model.BiolarkResult;
import org.monarchinitiative.hpotextmining.model.DataBucket;
import org.monarchinitiative.hpotextmining.model.Term;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is responsible for displaying the results of performed text-mining analysis. It presents analyzed text and
 * highlights regions that contain text from which the putative HPO terms were identified.
 * <p>
 * Identified <em>YES</em> and <em>NOT</em> HPO terms are displayed on the right side of the screen as a set of checkboxes.
 * The user/biocurator is supposed to review the analyzed text and select those checkboxes that have been identified
 * correctly.
 * <p>
 * Created by Daniel Danis on 6/19/17.
 */
public class PresentController implements DialogController {

    private FXMLDialog dialog;

    @Autowired
    private Environment env;

    @Autowired
    private DataBucket dataBucket;

    /**
     * Header of html with defined CSS for displaying tooltips. Tooltips will contain HPO id and label.
     */
    private static final String HTML_BEGIN = "<html><style> .tooltip { position: relative; display: inline-block; border-bottom: 1px dotted black; }" +
            ".tooltip .tooltiptext { visibility: hidden; width: 230px; background-color: #555; color: #fff; text-align: center;" +
            " border-radius: 6px; padding: 5px 0; position: absolute; z-index: 1; bottom: 125%; left: 50%; margin-left: -60px;" +
            " opacity: 0; transition: opacity 1s; }" +
            ".tooltip .tooltiptext::after { content: \"\"; position: absolute; top: 100%; left: 50%; margin-left: -5px;" +
            " border-width: 5px; border-style: solid; border-color: #555 transparent transparent transparent; }" +
            ".tooltip:hover .tooltiptext { visibility: visible; opacity: 1;}" +
            "</style><body>" +
            "<h2>BioLark concept recognition</h2><p>";

    /**
     * Html template for highlighting the text based on which a HPO term has been identified. Contains two placeholders.
     * The initial space is intentional (prevents lack of space between words with series of hits).
     * <ol>
     * <li>Text based on which a HPO term has been identified</li>
     * <li>Tooltip text</li>
     * </ol>
     */
    private static final String HIGHLIGHTED_TEMPLATE = " <b><span class=\"tooltip\" style=\"color:red\">%s" +
            "<span class=\"tooltiptext\">%s</span></span></b>";

    /**
     * Template for tooltips which appear when cursor hovers over highlighted terms.
     */
    private static final String TOOLTIP_TEMPLATE = "%s\n%s";

    @FXML
    private WebView webView;

    @FXML
    private VBox yesTermsVBox;

    @FXML
    private VBox notTermsVBox;

    /**
     * End of analysis.
     */
    @FXML
    void doneButtonClicked() {
        dataBucket.addApprovedYesTerms(getApprovedYesTerms());
        dataBucket.addApprovedNotTerms(getApprovedNotTerms());
        Platform.runLater(() -> dialog.close()); // enforce closing the dialog on JavaFX application thread
    }

    /**
     * Array of generated checkboxes corresponding to identified <em>YES</em> HPO terms.
     */
    private CheckBox[] yesTerms;

    /**
     * Array of generated checkboxes corresponding to identified <em>NOT</em> HPO terms.
     */
    private CheckBox[] notTerms;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        yesTerms = dataBucket.getIntermediateResults().stream()
                .filter(result -> !result.isNegated())
                .map(result -> result.getTerm().getLabel())
                .distinct()
                .sorted()
                .map(PresentController::checkBoxFactory)
                .toArray(CheckBox[]::new);
        yesTermsVBox.getChildren().addAll(yesTerms);

        notTerms = dataBucket.getIntermediateResults().stream()
                .filter(BiolarkResult::isNegated)
                .map(result -> result.getTerm().getLabel())
                .distinct()
                .sorted()
                .map(PresentController::checkBoxFactory)
                .toArray(CheckBox[]::new);
        notTermsVBox.getChildren().addAll(notTerms);

        String html = colorizeHTML(dataBucket.getIntermediateResults(), dataBucket.getMinedText());
        WebEngine engine = webView.getEngine();
        engine.loadContent(html);
    }

    /**
     * Return set of <em>YES</em> {@link Term} objects which have been approved by the curator.
     *
     * @return set of {@link Term} objects
     */
    Set<Term> getApprovedYesTerms() {
        Set<String> selected = Arrays.stream(yesTerms)
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.toSet());

        // filter all results to get only ones corresponding to selected checkboxes
        return dataBucket.getIntermediateResults().stream()
                .filter(result -> !result.isNegated())
                .filter(result -> selected.contains(result.getTerm().getLabel()))
                .map(BiolarkResult::getTerm)
                .collect(Collectors.toSet());
    }

    /**
     * Return set of <em>NOT</em> {@link Term} objects which have been approved by the curator.
     *
     * @return set of {@link Term} objects
     */
    Set<Term> getApprovedNotTerms() {
        Set<String> selected = Arrays.stream(notTerms)
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.toSet());

        // filter all results to get only ones corresponding to selected checkboxes
        return dataBucket.getIntermediateResults().stream()
                .filter(BiolarkResult::isNegated)
                .filter(result -> selected.contains(result.getTerm().getLabel()))
                .map(BiolarkResult::getTerm)
                .collect(Collectors.toSet());
    }

    /**
     * Use set of {@link BiolarkResult} objects to colorize regions of analyzed text based on which the HPO terms have
     * been identified. Generate HTML content that will be presented to the curator.
     *
     * @param resultSet set of {@link BiolarkResult} objects.
     * @param minedText string with mined text.
     * @return html content to be presented to the curator.
     */
    static String colorizeHTML(Set<BiolarkResult> resultSet, String minedText) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append(HTML_BEGIN);

        // sort to process minedText sequentially.
        List<BiolarkResult> results = resultSet.stream()
                .sorted(BiolarkResult.compareByStart())
                .collect(Collectors.toList());

        int offset = 0;
        for (BiolarkResult result : results) {
            int start = result.getStart() < offset ? offset : result.getStart();
            // htmlBuilder.append(minedText.substring(offset, result.getStart())); // unhighlighted text
            htmlBuilder.append(minedText.substring(offset, start)); // unhighlighted text
            start = Math.max(offset+1, result.getStart());
            StringBuilder append = htmlBuilder.append(
            // highlighted text
                    String.format(HIGHLIGHTED_TEMPLATE,
                            minedText.substring(start, result.getEnd()),
                            //minedText.substring(result.getStart(), result.getEnd()),
                            // tooltip text -> HPO id & label
                            String.format(TOOLTIP_TEMPLATE,
                                    result.getTerm().getId(), result.getTerm().getLabel()))
            );

            offset = result.getEnd();
        }

        // process last part of mined text, if there is any
        htmlBuilder.append(minedText.substring(offset));
        htmlBuilder.append("</p>");
        htmlBuilder.append("</body></html>");

        return htmlBuilder.toString();
    }


    /**
     * @inheritDocs
     * @param dialog The {@link FXMLDialog} instance which represents an independent window.
     */
    @Override
    public void setDialog(FXMLDialog dialog) {
        this.dialog = dialog;
    }


    /**
     * Create checkbox on the fly applying desired style, padding, etc.
     *
     * @param text - title of created CheckBox
     * @return created {@link CheckBox} instance
     */
    private static CheckBox checkBoxFactory(String text) {
        CheckBox cb = new CheckBox(text);
        cb.setPadding(new Insets(5));
        return cb;
    }
}
