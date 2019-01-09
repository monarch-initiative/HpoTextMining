package com.github.monarchinitiative.hpotextmining.gui.controller;

import com.github.monarchinitiative.hpotextmining.core.miners.MinedTerm;
import com.github.monarchinitiative.hpotextmining.core.miners.TermMiner;
import com.github.monarchinitiative.hpotextmining.core.miners.biolark.BiolarkTermMiner;
import com.github.monarchinitiative.hpotextmining.core.miners.scigraph.SciGraphTermMiner;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.util.Callback;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Driver class/API for JavaFX presentation of HPO text mining.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @author <a href="mailto:aaron.zhangl@jax.org">Aaron Zhang</a>
 */
public class HpoTextMining {

    private static final Logger LOGGER = LoggerFactory.getLogger(HpoTextMining.class);

    // ---------------------------------- CONTROLLERS and PARENTS ------------------------------------------------------

    /**
     * Main parent, contains all GUI elements of the widget.
     */
    private final Parent mainParent;

    /**
     * Main controller, part of the API.
     */
    private Main main;

    /**
     * The query text is submitted here.
     */
    private Configure configure;

    private Node configureAnchorPane;

    /**
     * Results of text mining is presented here.
     */
    private Present present;

    private Node presentVBox;

    /**
     * Ontology is displayed in this controller as a tree.
     */
    private OntologyTree ontologyTree;

    /**
     * @param ontology        {@link Ontology} to use for text mining
     * @param miner           {@link TermMiner} to use for HPO text mining
     * @param executorService {@link ExecutorService} to use for asynchronous tasks
     * @param presentTerms    {@link Set} of {@link com.github.monarchinitiative.hpotextmining.gui.controller.Main.PhenotypeTerm}s
     *                        to display in the widget from the beginning
     * @throws IOException if the building process fails
     */
    private HpoTextMining(Ontology ontology, TermMiner miner, ExecutorService executorService, Set<Main.PhenotypeTerm> presentTerms) throws IOException {
        main = new Main();
        // Set up "Configure" part of the screen
        Consumer<Main.Signal> configureSignal = signal -> {
            switch (signal) {
                case DONE:
                    Set<Main.PhenotypeTerm> phenotypeTerms = configure.getTerms().stream()
                            .map(minedTermToPhenotypeTerm(ontology))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());
                    present.setResults(phenotypeTerms, configure.getQuery());
                    main.setTextMiningContent(presentVBox);
                    break;
                case FAILED:
                    LOGGER.warn("Sorry, text mining analysis failed."); // TODO - improve cancellation & failed handling
                    break;
                case CANCELLED:
                    LOGGER.warn("Text mining analysis cancelled");
                    break;
            }
        };
        configure = new Configure(miner, executorService, configureSignal);

        // Set up "Present" part of the screen
        Consumer<Main.Signal> presentSignal = signal -> {
            switch (signal) {
                case DONE:
                    main.addPhenotypeTerms(present.getApprovedTerms());
                    main.setTextMiningContent(configureAnchorPane);
                    break;
                case FAILED:
                    LOGGER.warn("Sorry, text mining analysis failed."); // TODO - improve cancellation & failed handling);
                    break;
                case CANCELLED:
                    LOGGER.warn("Text mining analysis cancelled");
                    break;
                default:
                    LOGGER.warn("Unknown option '{}'", signal);
                    break;
            }
        };
        this.present = new Present(presentSignal, termId -> ontologyTree.focusOnTerm(ontology.getTermMap().get(termId)));

        // Set up "OntologyTree" part of the screen
        Consumer<Main.PhenotypeTerm> addHook = (main::addPhenotypeTerm);
        this.ontologyTree = new OntologyTree(ontology, addHook);

        // Simple controller factory treating controller created above as singletons.
        Callback<Class<?>, Object> controllerFactory = clazz -> {
            if (clazz.equals(Main.class)) {
                return main;
            } else if (clazz.equals(Configure.class)) {
                return configure;
            } else if (clazz.equals(Present.class)) {
                return present;
            } else if (clazz.equals(OntologyTree.class)) {
                return ontologyTree;
            } else {
                LOGGER.warn("Unknown class '{}' requested", clazz);
                return null;
            }
        };

        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("Main.fxml"));
        mainLoader.setControllerFactory(controllerFactory);
        mainParent = mainLoader.load();

        FXMLLoader configureLoader = new FXMLLoader(getClass().getResource("Configure.fxml"));
        configureLoader.setControllerFactory(controllerFactory);
        configureAnchorPane = configureLoader.load();
        main.setTextMiningContent(configureAnchorPane);

        FXMLLoader presentLoader = new FXMLLoader(getClass().getResource("Present.fxml"));
        presentLoader.setControllerFactory(controllerFactory);
        presentVBox = presentLoader.load();

        FXMLLoader ontologyTreeLoader = new FXMLLoader(getClass().getResource("OntologyTree.fxml"));
        ontologyTreeLoader.setControllerFactory(controllerFactory);
        main.setLeftStackPaneContent(ontologyTreeLoader.load());

        main.addPhenotypeTerms(presentTerms);
    }

    /**
     * @param ontology {@link Ontology} to use for creating {@link Term} from the String representation of termId
     * @return {@link Function} for mapping {@link MinedTerm} to {@link Main.PhenotypeTerm}. The function returns <code>null</code>
     * if the String representation of term ID is invalid or if there is not matching {@link Term} in the <code>ontology</code>
     */
    private static Function<MinedTerm, Main.PhenotypeTerm> minedTermToPhenotypeTerm(Ontology ontology) {
        return mt -> {
            TermId termId = TermId.of(mt.getTermId());
            if (!termId.getValue().startsWith("HP")) { // we are only working with HPO
                return null;
            }
            Term term = ontology.getTermMap().get(termId);
            if (term == null) {
                LOGGER.warn("There is not a term with id '{}' in the currently used ontology", termId.getValue());
                return null;
            } else {
                return new Main.PhenotypeTerm(term, mt);
            }
        };
    }

    /**
     * @return a new builder for {@link HpoTextMining} analysis
     */
    public static HpoTextMiningBuilder builder() {
        return new HpoTextMiningBuilder();
    }

    /**
     * @return {@link Parent} with all the GUI elements of the HpoTextMining widget
     */
    public Parent getMainParent() {
        return mainParent;
    }


    // -----------------------------------------------------------------------------------------------------------------

    /**
     * @return {@link Set} of {@link com.github.monarchinitiative.hpotextmining.gui.controller.Main.PhenotypeTerm}s
     * approved by the user
     */
    public Set<Main.PhenotypeTerm> getApprovedTerms() {
        return main.getPhenotypeTerms();
    }

    public static final class HpoTextMiningBuilder {

        private Ontology ontology;

        private URL biolarkServerUrl, sciGraphServerUrl;

        private TermMiner miner;

        private ExecutorService executorService;

        private Set<Main.PhenotypeTerm> terms = new HashSet<>();

        private HpoTextMiningBuilder() {
            // no-op
        }


        /**
         * @param ontology {@link Ontology} to work with (mandatory)
         * @return this {@link HpoTextMiningBuilder} instance
         */
        public HpoTextMiningBuilder withOntology(Ontology ontology) {
            this.ontology = ontology;
            return this;
        }

        /**
         * @param biolarkServerUrl {@link URL} pointing to Biolark server instance where the text mining should be
         *                         performed. SciGraph URL has precedence
         * @return this {@link HpoTextMiningBuilder} instance
         */
        public HpoTextMiningBuilder withBiolarkUrl(URL biolarkServerUrl) {
            this.biolarkServerUrl = biolarkServerUrl;
            return this;
        }

        /**
         * @param sciGraphServerUrl {@link URL} pointing to SciGraph server instance where the text mining should be
         *                          performed. SciGraph URL has precedence over Biolark URL
         * @return this {@link HpoTextMiningBuilder} instance
         */
        public HpoTextMiningBuilder withSciGraphUrl(URL sciGraphServerUrl) {
            this.sciGraphServerUrl = sciGraphServerUrl;
            return this;
        }

        /**
         * @param miner {@link TermMiner} to use for the text mining analysis. The miner has precedence over any URL
         *              (SciGraph, Biolark, etc..)
         * @return this {@link HpoTextMiningBuilder} instance
         */
        public HpoTextMiningBuilder withTermMiner(TermMiner miner) {
            this.miner = miner;
            return this;
        }

        /**
         * @param executorService {@link ExecutorService} to be used for asynchronous tasks (optional)
         * @return this {@link HpoTextMiningBuilder} instance
         */
        public HpoTextMiningBuilder withExecutorService(ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        /**
         * @param terms {@link Set} of terms to display in the widget from the beginning
         * @return this {@link HpoTextMiningBuilder} instance
         */
        public HpoTextMiningBuilder withPhenotypeTerms(Set<Main.PhenotypeTerm> terms) {
            this.terms.addAll(terms);
            return this;
        }

        /**
         * @return a new {@link HpoTextMining} instance
         * @throws IOException in case if the building fails
         */
        public HpoTextMining build() throws IOException {
            TermMiner usedMiner;
            if (this.miner != null) {
                usedMiner = miner;
            } else {
                if (sciGraphServerUrl != null) {
                    LOGGER.info("Using '{}' as url for text mining server", sciGraphServerUrl);
                    usedMiner = new SciGraphTermMiner(sciGraphServerUrl);
                } else {
                    if (biolarkServerUrl != null) {
                        usedMiner = new BiolarkTermMiner(biolarkServerUrl);
                    } else {
                        throw new NullPointerException("Neither SciGraph not Biolark URL was specified");
                    }
                }
            }

            Objects.requireNonNull(ontology, "Ontology must not be null");

            if (executorService == null) {
                executorService = Executors.newSingleThreadExecutor();
            }

            return new HpoTextMining(ontology, usedMiner, executorService, terms);
        }
    }
}
