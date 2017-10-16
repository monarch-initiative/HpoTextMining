package org.monarchinitiative.hpotextmining;

import com.genestalker.springscreen.core.FXMLDialog;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import ontologizer.ontology.Ontology;
import org.monarchinitiative.hpotextmining.controller.HPOAnalysisController;
import org.monarchinitiative.hpotextmining.model.PhenotypeTerm;
import org.monarchinitiative.hpotextmining.model.SimpleTextMiningResult;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.1.0
 * @since 0.1
 */
public class HPOTextMining {

    private final Window owner;

    private final Ontology ontology;

    private final URL textminingServer;

    private Set<PhenotypeTerm> terms = new HashSet<>();

    private String pmid;


    /**
     * Create instance of HPOTextMining.
     *
     * @param ontology         {@link Ontology} with HPO term hierarchy
     * @param textminingServer {@link URL} pointing to text mining server
     * @param owner            {@link Window} to be set as parental window. If owner is closed then also HPOTextMining dialog
     *                         will be closed
     */
    public HPOTextMining(Ontology ontology, URL textminingServer, Window owner) {
        this.ontology = ontology;
        this.textminingServer = textminingServer;
        this.owner = owner;
    }


    public String getPmid() {
        return pmid;
    }


    /**
     * Set PMID string.
     *
     * @param pmid String with PMID that will be displayed in dialog
     */
    public void setPmid(String pmid) {
        this.pmid = pmid;
    }


    /**
     * Get set of approved {@link PhenotypeTerm}s.
     *
     * @return copy of {@link Set} with approved {@link PhenotypeTerm}s
     */
    public Set<PhenotypeTerm> getTerms() {
        return new HashSet<>(terms);
    }


    /**
     * Add terms that will be displayed in the dialog at the beginning.
     *
     * @param terms {@link Set} of {@link PhenotypeTerm}s to display
     */
    public void addTerms(Set<PhenotypeTerm> terms) {
        this.terms.addAll(terms);
    }


    /**
     * Show dialog window where user can add/edit present HPO terms or run text-mining analysis on provided text.
     *
     * @return {@link TextMiningResult} with results of analysis
     */
    public TextMiningResult runAnalysis() {
        HPOAnalysisController controller = new HPOAnalysisController(ontology, textminingServer);
        controller.addPhenotypeTerms(terms);
        FXMLDialog dialog = new FXMLDialog.FXMLDialogBuilder()
                .setModality(Modality.NONE)
                .setFXML(getClass().getResource("/fxml/HPOAnalysisView.fxml"))
                .setDialogController(controller)
                .setStageStyle(StageStyle.DECORATED)
                .setOwner(owner)
                .build();
        dialog.showAndWait();
        return new SimpleTextMiningResult(controller.getPhenotypeTerms(), controller.getPmid());
    }

}
