package com.github.monarchinitiative.hpotextmining.demo;

import com.github.monarchinitiative.hpotextmining.core.miners.HPOMiner;
import com.github.monarchinitiative.hpotextmining.core.miners.biolark.BiolarkHPOMiner;
import com.github.monarchinitiative.hpotextmining.demo.controllers.Main;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ontologizer.io.obo.OBOParser;
import ontologizer.io.obo.OBOParserException;
import ontologizer.io.obo.OBOParserFileInput;
import ontologizer.ontology.Ontology;
import ontologizer.ontology.TermContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;

/**
 * Example usage of HpoTextMining module in a JavaFX App.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.1
 * @since 0.1
 */
public final class Play extends Application {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String BIOLARK_URL = "http://phenotyper.monarchinitiative.org:5678/cr/annotate";

    private static final String HPO_OBO_PATH = "/HP.obo";


    /**
     * To run analysis, you need to have an {@link Ontology} and {@link HPOMiner}. Here, the ontology is created from
     * OBO file that is bundled in a JAR file and the {@link BiolarkHPOMiner} is used as a miner.
     *
     * @param stage {@link Stage} to be used to display the app
     * @throws Exception blah
     */
    @Override
    public void start(Stage stage) throws Exception {

        HPOMiner miner = new BiolarkHPOMiner(new URL(BIOLARK_URL));
        Main main = new Main(miner);
        main.setOntology(getOntology());

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("Main.fxml"));
        loader.setControllerFactory(clazz -> main);
        Parent root = loader.load();

        stage.setScene(new Scene(root));
        stage.show();
    }


    private static Ontology getOntology() {
        try {
            OBOParser parser = new OBOParser(new OBOParserFileInput(Play.class.getResource(HPO_OBO_PATH).getFile()),
                    OBOParser.PARSE_DEFINITIONS);
            LOGGER.info(parser.doParse());
            TermContainer termContainer = new TermContainer(parser.getTermMap(), parser.getFormatVersion(), parser
                    .getDate());
            return Ontology.create(termContainer);
        } catch (IOException | OBOParserException e) {
            LOGGER.warn(e);
            return null;
        }
    }


    public static void main(String[] args) {
        launch(args);
    }

}
