package org.monarchinitiative.hpotextmining.demo;

import org.monarchinitiative.hpotextmining.gui.controller.HpoTextMining;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * Simple controller for demonstration of {@link HpoTextMining} widget.
 */
public class Controller {

    private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);

    private final Stage owner;

    private final URL scigraphUrl;

    private final ExecutorService executorService;

    private final Ontology ontology;

    Controller(Stage owner, URL scigraphUrl, ExecutorService executorService, Ontology ontology) {
        this.owner = owner;
        this.scigraphUrl = scigraphUrl;
        this.executorService = executorService;
        this.ontology = ontology;
    }


    @FXML
    public void runTextMIningAnalysisAction() {
        try {
            HpoTextMining hpoTextMining = HpoTextMining.builder()
                    .withSciGraphUrl(scigraphUrl)
                    .withOntology(ontology)
                    .withExecutorService(executorService)
                    .withPhenotypeTerms(new HashSet<>()) // maybe you want to display some terms from the beginning
                    .build();

            // show the text mining analysis dialog in the new stage/window
            Stage secondary = new Stage();
            secondary.initOwner(owner);
            secondary.setTitle("HPO text mining analysis");
            secondary.setScene(new Scene(hpoTextMining.getMainParent()));
            secondary.showAndWait();

            // do something with the results
            System.out.println(hpoTextMining.getApprovedTerms().stream()
                    .map(Object::toString)
                    .collect(Collectors.joining("\n", "Approved terms:\n", "")));
        } catch (IOException e) {
            LOGGER.warn("Error occured during text mining analysis", e);
        }

    }
}
