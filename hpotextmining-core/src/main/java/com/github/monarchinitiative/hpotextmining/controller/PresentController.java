package com.github.monarchinitiative.hpotextmining.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.github.monarchinitiative.hpotextmining.model.BiolarkResult;
import com.github.monarchinitiative.hpotextmining.model.SciGraphResult;
import com.github.monarchinitiative.hpotextmining.model.PhenotypeTerm;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermPrefix;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * This class is responsible for displaying the results of performed text-mining analysis. <p>The controller accepts
 * response from the server performing text-mining analysis in JSON format and the analyzed text. The analyzed text
 * with highlighted term-containing regions is presented to the user. Tooltips containing the HPO term id and name are
 * also created for the highlighted regions. After clicking on the highlighted region, corresponding term is selected
 * in the ontology TreeView (left part of the main window).
 * <p>
 * Identified <em>YES</em> and <em>NOT</em> HPO terms are displayed on the right side of the screen as a set of
 * checkboxes. The user/biocurator is supposed to review the analyzed text and select those checkboxes that have been
 * identified correctly.
 * <p>
 * Selected terms must be approved with <em>Add selected terms</em> button in order to add them into the model.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.1.0
 * @since 0.1
 */
public class PresentController implements Initializable {

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Header of html defining CSS & JavaScript for the presented text. CSS defines style for tooltips and
     * highlighted text. JavaScript code will allow focus on HPO term in the ontology treeview after clicking on the
     * highlighted text.
     */
    private static final String HTML_HEAD = "<html><head>" +
            "<style> .tooltip { position: relative; display: inline-block; border-bottom: 1px dotted black; }" +
            ".tooltip .tooltiptext { visibility: hidden; width: 230px; background-color: #555; color: #fff; " +
            "text-align: left;" +
            " border-radius: 6px; padding: 5px 0; position: absolute; z-index: 1; bottom: 125%; left: 50%; margin-left: -60px;" +
            " opacity: 0; transition: opacity 1s; }" +
            ".tooltip .tooltiptext::after { content: \"\"; position: absolute; top: 100%; left: 50%; margin-left: -5px;" +
            " border-width: 5px; border-style: solid; border-color: #555 transparent transparent transparent; }" +
            ".tooltip:hover .tooltiptext { visibility: visible; opacity: 1;}" +
            "</style>" +
            "<script>function focusOnTermJS(obj) {javafx_bridge.focusToTerm(obj);}</script>" +
            "</head>";

    private static final String HTML_BODY_BEGIN = "<body><h2>HPO text-mining analysis results:</h2><p>";

    private static final String HTML_BODY_END = "</p></body></html>";

    /**
     * Html template for highlighting the text based on which a HPO term has been identified. Contains three
     * placeholders: <ol>
     * <li>HPO term ID (param for javascript, it will be used to focus on HPO term in the ontology tree)</li>
     * <li>part of the query text based on which the HPO term has been identified</li>
     * <li>tooltip text</li> </ol>
     * The initial space is intentional, it prevents lack of space between words with series of hits.
     */
    private static final String HIGHLIGHTED_TEMPLATE = " " +
            "<span class=\"tooltip\" style=\"color:red;\" onclick=\"focusOnTermJS('%s')\">%s" +
            "<span class=\"tooltiptext\">%s</span></span>";

    /**
     * Template for tooltips which appear when cursor hovers over highlighted terms.
     */
    private static final String TOOLTIP_TEMPLATE = "%s\n%s";

    private static final TermPrefix HP_TERM_PREFIX= new TermPrefix("HP");

    private final Ontology ontology;

    /**
     * The GUI element responsible for presentation of analyzed text with highlighted regions.
     */
    @FXML
    private WebView webView;

    private WebEngine webEngine;

    private Consumer<HPOAnalysisController.Signal> signal;

    private Consumer<String> focusToTermHook = (e) -> LOGGER.warn("Unable to focus on term, hook is unset");

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


    void setSignal(Consumer<HPOAnalysisController.Signal> signal) {
        this.signal = signal;
    }


    /**
     * @param hook {@link Consumer} that will accept String with HPO term ID in order to show the term in ontology
     *             tree view
     */
    void setFocusToTermHook(Consumer<String> hook) {
        this.focusToTermHook = hook;
    }


    /**
     * Use set of {@link BiolarkResult} objects to colorize regions of analyzed (query) text based on which the HPO
     * terms have been identified. Generated HTML content will be presented to the curator in the {@link #webView}.
     *
     * @param resultSet Set of {@link BiolarkResult} objects
     * @param minedText String with mined (query) text
     * @return String in HTML format that will be displayed in the {@link #webView}
     */
    private String colorizeHTML(Set<BiolarkResult> resultSet, String minedText) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append(HTML_HEAD);
        htmlBuilder.append(HTML_BODY_BEGIN);

        // sort to process minedText sequentially.
        List<BiolarkResult> sortedResults = resultSet.stream()
                .sorted(BiolarkResult.compareByStart())
                .collect(Collectors.toList());

        int offset = 0;
        for (BiolarkResult result : sortedResults) {
            int start = result.getStart() < offset ? offset : result.getStart();
            htmlBuilder.append(minedText.substring(offset, start)); // unhighlighted text
            start = Math.max(offset + 1, result.getStart());
            Term term = ontology.getTermMap().get(result.getTerm().getTermId());
            if (term == null)
                continue;

            htmlBuilder.append(
                    // highlighted text
                    String.format(HIGHLIGHTED_TEMPLATE,
                            term.getId().toString(),
                            minedText.substring(start, result.getEnd()),
                            //minedText.substring(result.getStart(), result.getEnd()),
                            // tooltip text -> HPO id & label
                            String.format(TOOLTIP_TEMPLATE, term.getId().getIdWithPrefix(), term.getName())));

            offset = result.getEnd();
        }

        // process last part of mined text, if there is any
        htmlBuilder.append(minedText.substring(offset));
        htmlBuilder.append(HTML_BODY_END);
        // get rid of double spaces
        return htmlBuilder.toString().replaceAll("\\s{2,}", " ").trim();
    }

    /**
     * Similar to above but this one works for results from SciGraph server
     * @Author Aaron Zhang
     */
    private String colorizeHTML4ciGraph(Set<BiolarkResult> resultSet, String minedText) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append(HTML_HEAD);
        htmlBuilder.append(HTML_BODY_BEGIN);

        // sort to process minedText sequentially.
        List<BiolarkResult> sortedResults = resultSet.stream()
                .sorted(BiolarkResult.compareByStart())
                .collect(Collectors.toList());

        int offset = 0;
        for (BiolarkResult result : sortedResults) {
            int start = result.getStart() < offset ? offset : result.getStart();
            htmlBuilder.append(minedText.substring(offset, start)); // unhighlighted text
            //start = Math.max(offset + 1, result.getStart());
            //Term id is a string such as "HP:0000822"
            Term term = ontology.getTermMap().get(result.getTerm().getTermId());
            if (term == null) {
                continue;
            }
            htmlBuilder.append(
                    // highlighted text
                    String.format(HIGHLIGHTED_TEMPLATE,
                            term.getId().toString(),
                            minedText.substring(start, result.getEnd()),
                            //minedText.substring(result.getStart(), result.getEnd()),
                            // tooltip text -> HPO id & label
                            String.format(TOOLTIP_TEMPLATE, term.getId().getIdWithPrefix(), term.getName())));

            offset = result.getEnd();
        }

        // process last part of mined text, if there is any
        htmlBuilder.append(minedText.substring(offset));
        htmlBuilder.append(HTML_BODY_END);
        // get rid of double spaces
        return htmlBuilder.toString().replaceAll("\\s{2,}", " ").trim();
    }

    /**
     * End of analysis. Add approved terms into {@link HPOAnalysisController#hpoTermsTableView} and display configure
     * Dialog to allow next round of text-mining analysis.
     */
    @FXML
    void addTermsButtonAction() {
        signal.accept(HPOAnalysisController.Signal.DONE);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        webEngine = webView.getEngine();
        // register JavaBridge object in the JavaScript engine of the webEngine
        webEngine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject win = (JSObject) webEngine.executeScript("window");
                win.setMember("javafx_bridge", new JavaBridge());
                // redirect JavaScript console.LOGGER() to sysout defined in the JavaBridge
                webEngine.executeScript("console.log = function(message) {javafx_bridge.log(message);};");
            }
        });
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
            results.addAll(decodePayload(jsonResult, minedText));//switch to Monarch SciGraph Server
        } catch (IOException e) {
            LOGGER.warn(e);
            e.printStackTrace();
        }
        //The following section add labels from ontology. we currently use the labels provided by scigraph
//        results.forEach(r -> {
//            Term term = ontology.getTermMap().get(r.getTerm().getTermId());
//            r.getTerm().setLabel(term.getName());
//        });

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

        //String html = colorizeHTML(results, minedText);
        String html = colorizeHTML4ciGraph(results, minedText);
        webEngine.loadContent(html);
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
                .map(term -> new PhenotypeTerm(ontology.getTermMap().get(term.getTerm().getTermId()), true))
                .collect(Collectors.toSet()));
        all.addAll(results.stream()
                .filter(result -> notApproved.contains(result.getTerm().getLabel())) // create non-present PhenotypeTerm
                .map(term -> new PhenotypeTerm(ontology.getTermMap().get(term.getTerm().getTermId()), false))
                .collect(Collectors.toSet()));
        return all;
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
     * Parse JSON string from Tudor Server into set of intermediate result objects.
     *
     * @param jsonResponse JSON string to be parsed.
     * @return set of {@link BiolarkResult} objects.
     * @throws IOException in case of parsing problems
     */
    private static Set<BiolarkResult> decodePayload(String jsonResponse) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        CollectionType javaType = mapper.getTypeFactory().constructCollectionType(Set.class, BiolarkResult.class);
        return mapper.readValue(jsonResponse, javaType);
    }

    /**
     * Parse JSON string from Monarch SciGraph Server into set of intermediate result objects.
     *
     * @Author Aaron Zhang
     * @param jsonResponse JSON string to be parsed.
     * @return set of {@link BiolarkResult} objects.
     * @throws IOException in case of parsing problems
     */
    private static Set<BiolarkResult> decodePayload(String jsonResponse, String queryText) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        //map json result into SciGraphResult objects
        SciGraphResult[] sciGraphResults = mapper.readValue(jsonResponse, SciGraphResult[].class);
        //convert into BiolarkResults and return hpo terms
        return Arrays.stream(sciGraphResults).map(o -> SciGraphResult.toBiolarkResult(o, queryText))
                .filter(o -> o.getSource().startsWith("HP")).collect(Collectors.toSet());
    }


    /**
     * This class is the bridge between JavaScript run in the {@link #webView} and Java code.
     */
    public class JavaBridge {

        public void log(String message) {
            LOGGER.info(message);
        }


        /**
         * @param termId String like HP:1234567
         */
        public void focusToTerm(String termId) {
            LOGGER.debug("Focusing on term with ID {}", termId);
            focusToTermHook.accept(termId);
        }
    }
}
