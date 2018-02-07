package com.github.monarchinitiative.hpotextmining.gui.controllers;

import com.github.monarchinitiative.hpotextmining.core.miners.HPOMiner;
import com.github.monarchinitiative.hpotextmining.core.miners.MinedTerm;
import com.github.monarchinitiative.hpotextmining.gui.resources.OptionalResources;
import com.google.common.collect.ComparisonChain;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import ontologizer.ontology.Term;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.1
 * @since 0.2
 */
public final class Present {

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

    private static final String MINING_IN_PROGRESS = "<body><em>Performing text-mining analysis...</em><p>";

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

    private final HPOMiner miner;

    private final ExecutorService executor;

    private final OptionalResources optionalResources;

    @FXML
    public WebView webView;

    private WebEngine webEngine;

    @FXML
    public VBox yesTermsVBox;

    @FXML
    public VBox notTermsVBox;

    @FXML
    public Button cancelButton;

    @FXML
    public ProgressIndicator miningProgressIndicator;

    @FXML
    public Label miningProgressLabel;

    /**
     * Array of generated checkboxes corresponding to identified <em>YES</em> HPO terms.
     */
    private CheckBox[] yesTerms;

    /**
     * Array of generated checkboxes corresponding to identified <em>NOT</em> HPO terms.
     */
    private CheckBox[] notTerms;

    private MiningService miningService;

    private Consumer<Set<Main.PhenotypeTerm>> resultHook =
            terms -> LOGGER.warn("Unable to accept terms, resultHook is unset");

    private Consumer<String> focusToTermHook = term -> LOGGER.warn("Unable to focus on term, focusToTermHook is unset");


    @Inject
    public Present(HPOMiner miner, ExecutorService executor, OptionalResources optionalResources) {
        this.miner = miner;
        this.executor = executor;
        this.optionalResources = optionalResources;
    }


    public void initialize() {
        miningService = new MiningService();
        miningProgressIndicator.progressProperty().bind(miningService.progressProperty());
        miningProgressLabel.textProperty().bind(miningService.messageProperty());
        miningService.setOnRunning(e -> {
            webEngine.loadContent(HTML_HEAD + MINING_IN_PROGRESS + HTML_BODY_END);
            cancelButton.setDisable(false);
        });
        miningService.setOnSucceeded(e -> {
            cancelButton.setDisable(true);
            displayResults(miningService);
        });
        miningService.setOnCancelled(e -> {
            LOGGER.info("Cancelling task on thread " + Thread.currentThread().getName());
            resultHook.accept(new HashSet<>());
        });
        miningService.setOnFailed(e -> {
            LOGGER.info("Failed task on thread " + Thread.currentThread().getName());
        });
        miningService.setExecutor(executor);

        // configure WebView & WebEngine - a place for displaying mined text with highlighted terms
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


    void setFocusToTermHook(Consumer<String> focusToTermHook) {
        this.focusToTermHook = focusToTermHook;
    }


    void setResultHook(Consumer<Set<Main.PhenotypeTerm>> resultHook) {
        this.resultHook = resultHook;
    }


    @FXML
    void addTermsButtonAction() {
        // create sets of terms selected by user by checking a corresponding CheckBox
        Set<Term> present = Arrays.stream(yesTerms)
                .filter(CheckBox::isSelected)
                .map(cb -> ((Term) cb.getUserData()))
                .collect(Collectors.toSet());
        Set<Term> unpresent = Arrays.stream(notTerms)
                .filter(CheckBox::isSelected)
                .map(cb -> ((Term) cb.getUserData()))
                .collect(Collectors.toSet());

        // map terms to PhenotypeTerm object, as requested in resultHook
        Set<Main.PhenotypeTerm> all = new HashSet<>();
        present.stream()
                .map(term -> new Main.PhenotypeTerm(term, true))
                .forEach(all::add);
        unpresent.stream()
                .map(term -> new Main.PhenotypeTerm(term, false))
                .forEach(all::add);
        resultHook.accept(all);
    }


    void setQueryText(String query) {
        if (!miningService.getState().equals(Worker.State.READY)) {
            Worker.State state = miningService.getState();
            switch (state) {
                case CANCELLED:
                case FAILED:
                case SUCCEEDED:
                    miningService.reset();
                    break;
                default:
                    return; // Service is not ready, we can't start
            }
        }
        miningService.setQuery(query);
        miningService.setMiner(miner);
        miningService.start();
    }


    @FXML
    public void cancelButtonAction() {
        miningService.cancel();
    }


    private void displayResults(MiningService service) {

        final Set<MinedTerm> terms = service.getValue();
        final String query = service.getQuery();

        yesTerms = terms.stream()
                .filter(MinedTerm::isPresent)
                .map(term -> optionalResources.getOntology().getTerm(term.getTermId()))
                .distinct()
                .sorted(termComparator())
                .map(Present::checkBoxFactory)
                .toArray(CheckBox[]::new);

        notTerms = terms.stream()
                .filter(term -> !term.isPresent())
                .map(term -> optionalResources.getOntology().getTerm(term.getTermId()))
                .distinct()
                .sorted(termComparator())
                .map(Present::checkBoxFactory)
                .toArray(CheckBox[]::new);

        yesTermsVBox.getChildren().clear();
        notTermsVBox.getChildren().clear();
        yesTermsVBox.getChildren().addAll(yesTerms);
        notTermsVBox.getChildren().addAll(notTerms);

        String html = colorizeHTML(terms, query);
        webEngine.loadContent(html);
    }


    /**
     * Use set of {@link MinedTerm} objects to colorize regions of analyzed (query) text based on which the HPO
     * terms have been identified. Generated HTML content will be presented to the curator in the {@link #webView}.
     *
     * @param resultSet Set of {@link MinedTerm} objects
     * @param minedText String with mined (query) text
     * @return String in HTML format that will be displayed in the {@link #webView}
     */
    private String colorizeHTML(Set<MinedTerm> resultSet, String minedText) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append(HTML_HEAD);
        htmlBuilder.append(HTML_BODY_BEGIN);

        // sort to process minedText sequentially.
        List<MinedTerm> sortedResults = resultSet.stream()
                .sorted(MinedTerm.compareByBegin())
                .collect(Collectors.toList());

        int offset = 0;
        for (MinedTerm result : sortedResults) {
            int start = result.getBegin() < offset ? offset : result.getBegin();
            htmlBuilder.append(minedText.substring(offset, start)); // unhighlighted text
            start = Math.max(offset + 1, result.getBegin());
            Term term = optionalResources.getOntology().getTerm(result.getTermId());
            if (term == null)
                continue;

            htmlBuilder.append(
                    // highlighted text
                    String.format(HIGHLIGHTED_TEMPLATE,
                            term.getIDAsString(),
                            minedText.substring(start, result.getEnd()),
                            //minedText.substring(result.getStart(), result.getEnd()),
                            // tooltip text -> HPO id & label
                            String.format(TOOLTIP_TEMPLATE, term.getIDAsString(), term.getName().toString())));

            offset = result.getEnd();
        }

        // process last part of mined text, if there is any
        htmlBuilder.append(minedText.substring(offset));
        htmlBuilder.append(HTML_BODY_END);
        // get rid of double spaces
        return htmlBuilder.toString().replaceAll("\\s{2,}", " ").trim();
    }


    /**
     * Create checkbox on the fly applying desired style, padding, etc. The {@link Term} is stored in CheckBox's user
     * data.
     *
     * @param term - {@link Term} to be represented by created CheckBox
     * @return created {@link CheckBox} instance
     */
    private static CheckBox checkBoxFactory(Term term) {
        CheckBox cb = new CheckBox(term.getName().toString());
        cb.setUserData(term);
        cb.setPadding(new Insets(5));
        return cb;
    }


    /**
     * @return {@link Comparator} for comparing {@link Term}s alphabetically in ascending order by their label (name)
     */
    private static Comparator<Term> termComparator() {
        return (l, r) -> ComparisonChain.start().compare(l.getName().toString(), r.getName().toString()).result();
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

    class MiningService extends Service<Set<MinedTerm>> {

        private final ObjectProperty<HPOMiner> miner = new SimpleObjectProperty<>(this, "miner");

        private final StringProperty query = new SimpleStringProperty(this, "query", "");


        HPOMiner getMiner() {
            return miner.get();
        }


        void setMiner(HPOMiner miner) {
            this.miner.set(miner);
        }


        String getQuery() {
            return query.get();
        }


        void setQuery(String query) {
            this.query.set(query);
        }


        @Override
        protected Task<Set<MinedTerm>> createTask() {
            return new Task<Set<MinedTerm>>() {

                @Override
                protected Set<MinedTerm> call() {
                    final String query = getQuery();
                    final HPOMiner miner = getMiner();
                    Set<MinedTerm> terms = new HashSet<>();
                    try {
                        terms.addAll(miner.doMining(query));
                        updateProgress(1, 1);
                    } catch (Exception ex) {
                        LOGGER.warn(ex);
                        failed();
                        updateMessage(ex.toString());
                        updateProgress(0, 1);
                    }
                    return terms;
                }
            };
        }

    }
}
