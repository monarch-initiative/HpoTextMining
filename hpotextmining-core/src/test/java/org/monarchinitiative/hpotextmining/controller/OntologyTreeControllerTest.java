package org.monarchinitiative.hpotextmining.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import ontologizer.io.obo.OBOParser;
import ontologizer.io.obo.OBOParserException;
import ontologizer.io.obo.OBOParserFileInput;
import ontologizer.ontology.Ontology;
import ontologizer.ontology.TermContainer;
import org.junit.BeforeClass;
import org.junit.Test;
import org.monarchinitiative.hpotextmining.HPOTextMining;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test functionality of {@link OntologyTreeController} class.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.1.0
 * @since 0.1
 */
public class OntologyTreeControllerTest extends ApplicationTest {

    private static final String oboPath = OntologyTreeControllerTest.class.getResource("/HP.obo").getPath();

    private static Ontology ontology;

    private OntologyTreeController controller;


    /**
     * Test autocompletion functionality in {@link OntologyTreeController#searchTextField}.
     *
     * @throws Exception bla
     */
    @Test
    public void testAutocompletionAndSearch() throws Exception {
        assertTrue(controller.getSelectedTerm() == null);
        doubleClickOn("#searchTextField").write("hepatosplenomegaly")
                .sleep(800).moveBy(10, 30).clickOn(MouseButton.PRIMARY)
                .clickOn("#goButton");
        assertEquals("HP:0001433", controller.getSelectedTerm().getValue().getID().toString());
        doubleClickOn("#searchTextField").write("hyperten")
                .sleep(800).moveBy(10, 80).clickOn(MouseButton.PRIMARY)
                .clickOn("#goButton");
        assertEquals("HP:0000822", controller.getSelectedTerm().getValue().getID().toString());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage stage) throws Exception {
        controller = new OntologyTreeController(ontology, pt -> {/* do nothing*/});
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TestOntologyTreeView.fxml"));
        loader.setControllerFactory(param -> controller);
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }


    @BeforeClass
    public static void beforeClassSetUp() throws Exception {
        ontology = ontology();
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