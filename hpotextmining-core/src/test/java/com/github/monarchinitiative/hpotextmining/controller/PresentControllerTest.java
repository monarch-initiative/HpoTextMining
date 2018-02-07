package com.github.monarchinitiative.hpotextmining.controller;

//import com.github.monarchinitiative.hpotextmining.HPOTextMining;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ontologizer.io.obo.OBOParser;
import ontologizer.io.obo.OBOParserException;
import ontologizer.io.obo.OBOParserFileInput;
import ontologizer.ontology.Ontology;
import ontologizer.ontology.TermContainer;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * Tests of the {@link PresentController}, not yet implemented.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.0
 * @since 0.2.0
 */
@Ignore
public class PresentControllerTest extends ApplicationTest {

    private static final String oboPath = PresentControllerTest.class.getResource("/HP.obo").getPath();

    private static Ontology ontology;

    private static String queryText, jsonResponse;

    /**
     * Tested instance
     */
//    private PresentController controller;


    @Test
    public void justForFun() throws Exception {
//        Platform.runLater(() -> controller.setResults(jsonResponse, queryText));
//        sleep(3000);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage stage) throws Exception {
//        controller = new PresentController(ontology);
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TestPresentView.fxml"));
//        loader.setControllerFactory(param -> controller);
//        Scene scene = new Scene(loader.load());
//        stage.setScene(scene);
//        stage.show();
    }


    @BeforeClass
    public static void beforeClassSetUp() throws Exception {
        ontology = ontology();
        BufferedReader queryTextReader = new BufferedReader(
                new InputStreamReader(PresentControllerTest.class.getResourceAsStream("/payload.txt")));
        queryText = queryTextReader.lines().collect(Collectors.joining("\n"));
        queryTextReader.close();

        BufferedReader jsonResponseReader = new BufferedReader(
                new InputStreamReader(PresentControllerTest.class.getResourceAsStream("/jsonResponse.txt")));
        jsonResponse = jsonResponseReader.lines().collect(Collectors.joining("\n"));
        jsonResponseReader.close();
    }


    /**
     * Parse <em>*.obo</em> file and create {@link Ontology} for {@link HPOTextMining} object.
     *
     * @return {@link Ontology} representing the hierarchy of the ONTOLOGY
     * @throws IOException        if the path to <em>*.obo</em> ONTOLOGY file is incorrect
     * @throws OBOParserException if there is a problem with parsing of the ONTOLOGY
     */
    private static Ontology ontology() throws IOException, OBOParserException {
        OBOParser parser = new OBOParser(new OBOParserFileInput(oboPath),
                OBOParser.PARSE_DEFINITIONS);
        parser.doParse();
        TermContainer termContainer = new TermContainer(parser.getTermMap(), parser.getFormatVersion(), parser
                .getDate());
        return Ontology.create(termContainer);
    }

}