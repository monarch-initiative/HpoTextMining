package org.monarchinitiative.hpotextmining;

import com.genestalker.springscreen.core.FXMLDialog;
import javafx.stage.Stage;
import ontologizer.ontology.Ontology;
import org.monarchinitiative.hpotextmining.application.HPOAnalysisController;
import org.monarchinitiative.hpotextmining.model.PhenotypeTerm;
import org.monarchinitiative.hpotextmining.model.TextMiningResult;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Driver class for Text-mining analysis. See {@link Main} for information how to plug the analysis into existing Java
 * FX program. Created by Daniel Danis on 6/19/17.
 */
public class TextMiningAnalysis {

    private static final String DIALOG_TITLE = "Text-mining analysis";

    private final Ontology ontology;

    private final URL textMiningServer;

    private final String pmid;

    private final Set<PhenotypeTerm> knownTerms;

    private TextMiningAnalysis(Ontology ontology, URL textMiningServer, String pmid, Set<PhenotypeTerm> knownTerms) {
        this.ontology = ontology;
        this.textMiningServer = textMiningServer;
        this.pmid = pmid;
        this.knownTerms = knownTerms;
    }

    /**
     * Run the text-mining analysis (and have fun).
     *
     * @param primaryStage instance of {@link Stage} to be used as owner window for text-mining analysis pop-up
     *                     windows.
     * @return Optional of {@link TextMiningResult}.
     */
    public TextMiningResult run(Stage primaryStage) {

        HPOAnalysisController hpoAnalysisController = new HPOAnalysisController(ontology, textMiningServer, pmid, knownTerms);

        FXMLDialog hpoAnalysisDialog = new FXMLDialog.FXMLDialogBuilder().setDialogController(hpoAnalysisController)
                .setFXML(getClass().getResource("/fxml/HPOAnalysisView.fxml")).setOwner(primaryStage).build();
        hpoAnalysisDialog.setTitle(DIALOG_TITLE);
        hpoAnalysisDialog.showAndWait();
        return hpoAnalysisController.getResults();
    }

    public static class TextMiningAnalysisBuilder {

        private Ontology ontology;

        private URL textMiningServer;

        private String pmid = "";

        private Set<PhenotypeTerm> phenotypeTerms = new HashSet<>();

        public TextMiningAnalysisBuilder setOntology(Ontology ontology) {
            this.ontology = ontology;
            return this;
        }

        public TextMiningAnalysisBuilder setURL(URL textMiningServer) {
            this.textMiningServer = textMiningServer;
            return this;
        }

        public TextMiningAnalysisBuilder setPmid(String pmid) {
            this.pmid = pmid;
            return this;
        }

        public TextMiningAnalysisBuilder setPhenotypeTerms(Set<PhenotypeTerm> terms) {
            this.phenotypeTerms.addAll(terms);
            return this;
        }

        public TextMiningAnalysis build() {
            return new TextMiningAnalysis(ontology, textMiningServer, pmid, phenotypeTerms);
        }

    }
}
