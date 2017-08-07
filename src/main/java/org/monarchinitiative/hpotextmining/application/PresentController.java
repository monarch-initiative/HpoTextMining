package org.monarchinitiative.hpotextmining.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import ontologizer.ontology.Ontology;
import ontologizer.ontology.Term;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.hpotextmining.model.BiolarkResult;
import org.monarchinitiative.hpotextmining.model.HTMSignal;
import org.monarchinitiative.hpotextmining.model.PhenotypeTerm;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * This class is responsible for displaying the results of performed text-mining analysis. <p>The controller accepts
 * response from the server performing text-mining analysis in JSON format and the analyzed text. The analyzed text is
 * presented and regions of text based on which the putative HPO terms were identified are highlighted. Tooltips
 * containing the HPO term id & name are also created for these regions.
 * <p>
 * Identified <em>YES</em> and <em>NOT</em> HPO terms are displayed on the right side of the screen as a set of
 * checkboxes. The user/biocurator is supposed to review the analyzed text and select those checkboxes that have been
 * identified correctly. After selection, it is necessary
 * <p>
 * Created by Daniel Danis on 6/19/17.
 */
public class PresentController implements Initializable {

    private static final Logger log = LogManager.getLogger(PresentController.class);

    /**
     * Header of html with defined CSS for displaying tooltips. Tooltips will contain HPO id and label.
     */
    private static final String HTML_BEGIN = "<html><style> .tooltip { position: relative; display: inline-block; border-bottom: 1px dotted black; }" +
            ".tooltip .tooltiptext { visibility: hidden; width: 230px; background-color: #555; color: #fff; " +
            "text-align: left;" +
            " border-radius: 6px; padding: 5px 0; position: absolute; z-index: 1; bottom: 125%; left: 50%; margin-left: -60px;" +
            " opacity: 0; transition: opacity 1s; }" +
            ".tooltip .tooltiptext::after { content: \"\"; position: absolute; top: 100%; left: 50%; margin-left: -5px;" +
            " border-width: 5px; border-style: solid; border-color: #555 transparent transparent transparent; }" +
            ".tooltip:hover .tooltiptext { visibility: visible; opacity: 1;}" +
            "</style><body>" +
            "<h2>HPO text-mining analysis results:</h2><p>";

    /**
     * Html template for highlighting the text based on which a HPO term has been identified. Contains two placeholders.
     * The initial space is intentional (prevents lack of space between words with series of hits). <ol> <li>Text based
     * on which a HPO term has been identified</li> <li>Tooltip text</li> </ol>
     */
    private static final String HIGHLIGHTED_TEMPLATE = " <b><span class=\"tooltip\" style=\"color:red\">%s" +
            "<span class=\"tooltiptext\">%s</span></span></b>";

    /**
     * Template for tooltips which appear when cursor hovers over highlighted terms.
     */
    private static final String TOOLTIP_TEMPLATE = "%s\n%s";

    private final Ontology ontology;

    /**
     * The GUI element responsible for presentation of analyzed text with highlighted regions.
     */
    @FXML
    private WebView webView;

    private Consumer<HTMSignal> signal;

    /**
     * Box on the right side of the screen where "YES" Terms will be added.
     */
    @FXML
    private VBox yesTermsVBox;

    /**
     * Box on the right side of the screen where "NOT" Terms will be added.
     */
    @FXML
    private VBox notTermsVBox;

    /**
     * Array of generated checkboxes corresponding to identified <em>YES</em> HPO terms.
     */
    private CheckBox[] yesTerms;

    /**
     * Array of generated checkboxes corresponding to identified <em>NOT</em> HPO terms.
     */
    private CheckBox[] notTerms;

    /**
     * Store the received
     */
    private Set<BiolarkResult> results = new HashSet<>();

    public PresentController(Ontology ontology) {
        this.ontology = ontology;
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

    /**
     * Parse JSON string into set of intermediate result objects.
     *
     * @param jsonResponse JSON string to be parsed.
     * @return set of {@link BiolarkResult} objects.
     * @throws IOException
     */
    static Set<BiolarkResult> decodePayload(String jsonResponse) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        CollectionType javaType = mapper.getTypeFactory().constructCollectionType(Set.class, BiolarkResult.class);
        return mapper.readValue(jsonResponse, javaType);
    }

    void setSignal(Consumer<HTMSignal> signal) {
        this.signal = signal;
    }

    /**
     * Use set of {@link BiolarkResult} objects to colorize regions of analyzed text based on which the HPO terms have
     * been identified. Generate HTML content that will be presented to the curator.
     *
     * @param resultSet set of {@link BiolarkResult} objects.
     * @param minedText string with mined text.
     * @return html content to be presented to the curator.
     */
    String colorizeHTML(Set<BiolarkResult> resultSet, String minedText) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append(HTML_BEGIN);

        // sort to process minedText sequentially.
        List<BiolarkResult> sortedResults = resultSet.stream()
                .sorted(BiolarkResult.compareByStart())
                .collect(Collectors.toList());

        int offset = 0;
        for (BiolarkResult result : sortedResults) {
            int start = result.getStart() < offset ? offset : result.getStart();
            htmlBuilder.append(minedText.substring(offset, start)); // unhighlighted text
            start = Math.max(offset + 1, result.getStart());
            Term term = ontology.getTerm(result.getTerm().getId());

            htmlBuilder.append(
                    // highlighted text
                    String.format(HIGHLIGHTED_TEMPLATE, minedText.substring(start, result.getEnd()),
                            //minedText.substring(result.getStart(), result.getEnd()),
                            // tooltip text -> HPO id & label
                            String.format(TOOLTIP_TEMPLATE, term.getIDAsString(), term.getName().toString())));

            offset = result.getEnd();
        }

        // process last part of mined text, if there is any
        htmlBuilder.append(minedText.substring(offset));
        htmlBuilder.append("</p>");
        htmlBuilder.append("</body></html>");
        return htmlBuilder.toString().replaceAll("\\s{2,}", " ").trim();
    }

    /**
     * End of analysis. Add approved terms into {@link HPOAnalysisController#hpoTermsTableView} and display configure
     * Dialog to allow next round of text-mining analysis.
     */
    @FXML
    void addTermsButtonAction() {
        signal.accept(HTMSignal.DONE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // no-op
    }

    /**
     * The data that are about to be presented are set here. The String with JSON results are coming from the
     * text-mining analysis performing server while the mined text is the text submitted by the user in Configure Dialog
     * (controlled by {@link ConfigureController}).
     *
     * @param jsonResult String in JSON format containing the result of text-mining analysis.
     * @param minedText  String with the query text submitted by the user.
     */
    void setResults(String jsonResult, String minedText) {
        results.clear(); // clean-up before adding new data.
        try {
            results.addAll(decodePayload(jsonResult));
        } catch (IOException e) {
            log.warn(e);
            e.printStackTrace();
        }

        yesTerms = results.stream()
                .filter(result -> !result.isNegated())
                .map(result -> result.getTerm().getLabel())
                .distinct()
                .sorted()
                .map(PresentController::checkBoxFactory)
                .toArray(CheckBox[]::new);

        notTerms = results.stream()
                .filter(BiolarkResult::isNegated)
                .map(result -> result.getTerm().getLabel())
                .distinct()
                .sorted()
                .map(PresentController::checkBoxFactory)
                .toArray(CheckBox[]::new);

        yesTermsVBox.getChildren().clear();
        notTermsVBox.getChildren().clear();
        yesTermsVBox.getChildren().addAll(yesTerms);
        notTermsVBox.getChildren().addAll(notTerms);

        String html = colorizeHTML(results, minedText);
        WebEngine engine = webView.getEngine();
        engine.loadContent(html);
    }

    /**
     * Return the final set of <em>YES</em> & <em>NOT</em> {@link PhenotypeTerm} objects which have been approved by
     * curator by ticking the checkbox.
     *
     * @return {@link Set} of approved {@link PhenotypeTerm}s.
     */
    Set<PhenotypeTerm> getApprovedTerms() {
        Set<String> yesApproved = Arrays.stream(yesTerms)
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.toSet());
        Set<String> notApproved = Arrays.stream(notTerms)
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.toSet());

        Set<PhenotypeTerm> all = new HashSet<>();
        // filter all results to get only those corresponding to selected checkboxes
        all.addAll(results.stream()
                .filter(result -> yesApproved.contains(result.getTerm().getLabel())) // create present Phenotype
                .map(term -> new PhenotypeTerm(ontology.getTerm(term.getTerm().getId()), true))
                .collect(Collectors.toSet()));
        all.addAll(results.stream()
                .filter(result -> notApproved.contains(result.getTerm().getLabel())) // create non-present PhenotypeTerm
                .map(term -> new PhenotypeTerm(ontology.getTerm(term.getTerm().getId()), false))
                .collect(Collectors.toSet()));
        return all;
    }
}
