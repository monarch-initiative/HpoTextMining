package org.monarchinitiative.hpotextmining.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.monarchinitiative.phenol.io.obo.hpo.HpOboParser;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Example usage of HpoTextMining module in a JavaFX App.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.1
 * @since 0.1
 */
public class Play extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Play.class);

    private static final String SCIGRAPH_URL_STRING = "https://scigraph-ontology.monarchinitiative.org/scigraph/annotations/complete";

    private static final String HPO_OBO_PATH = "/HP.obo";

    private final URL scigraphUrl;

    private final Ontology ontology;

    private final ExecutorService executorService;

    public Play() throws Exception {
        HpOboParser parser = new HpOboParser(Play.class.getResourceAsStream(HPO_OBO_PATH));
        ontology = parser.parse();
        scigraphUrl = new URL(SCIGRAPH_URL_STRING);
        executorService = Executors.newSingleThreadExecutor();
    }


    public static void main(String[] args) {
        launch(args);
    }

    /**
     * @param stage {@link Stage} to be used to display the app
     * @throws Exception if anything wrong happens
     */
    @Override
    public void start(Stage stage) throws Exception {
        Controller controller = new Controller(stage, scigraphUrl, executorService, ontology);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Controller.fxml"));
        loader.setControllerFactory(clz -> controller);
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        executorService.shutdown();
    }
}
