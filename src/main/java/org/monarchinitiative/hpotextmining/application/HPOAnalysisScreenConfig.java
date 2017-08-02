package org.monarchinitiative.hpotextmining.application;

import com.genestalker.springscreen.core.FXMLDialog;
import javafx.scene.Parent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * Configuration of Beans that are used in HpoTextMining analysis. This configuration class should be imported into
 * ApplicationContext when HpoTextMining analysis is a part of a larger Spring-based project.<p>Then, in order to run
 * the analysis following beans must be available: <ul><li>{@link java.util.concurrent.ExecutorService} - to run tasks
 * in threads.</li><li>{@link ontologizer.ontology.Ontology} - to provide HPO hierarchy.</li></ul> <p>Created by Daniel
 * Danis on 6/19/17.
 */
@Configuration
public class HPOAnalysisScreenConfig {

    private Stage owner;

    public void setOwner(Stage owner) {
        this.owner = owner;
    }

    @Bean
    public FXMLDialog hpoAnalysisDialog() {
        return new FXMLDialog.FXMLDialogBuilder()
                .setDialogController(hpoAnalysisController())
                .setFXML(getClass().getResource("/fxml/HPOAnalysisView.fxml"))
                .setStageStyle(StageStyle.DECORATED)
                .setModality(Modality.APPLICATION_MODAL)
                .setOwner(owner)
                .build();
    }

    @Bean
    HPOAnalysisController hpoAnalysisController() {
        return new HPOAnalysisController(this);
    }

    @Bean
    Parent configureDialog() {
        return FXMLDialog.loadParent(configureController(), getClass().getResource("/fxml/ConfigureView.fxml"));
    }

    @Bean
    ConfigureController configureController() {
        return new ConfigureController(this);
    }

    @Bean
    Parent presentDialog() {
        return FXMLDialog.loadParent(presentController(), getClass().getResource("/fxml/PresentView.fxml"));
    }

    @Bean
    PresentController presentController() {
        return new PresentController(this);
    }

}
