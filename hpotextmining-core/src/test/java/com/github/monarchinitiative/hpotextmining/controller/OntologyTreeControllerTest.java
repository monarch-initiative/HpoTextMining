package com.github.monarchinitiative.hpotextmining.controller;

import com.github.monarchinitiative.hpotextmining.HPOTextMining;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.BeforeClass;
import org.junit.Test;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.io.obo.hpo.HpOboParser;
import org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.TermPrefix;
import org.testfx.framework.junit.ApplicationTest;

import java.io.File;
import java.io.IOException;
import java.util.Set;

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
        assertEquals("HP:0001433", controller.getSelectedTerm().getValue().getId().toString());
        doubleClickOn("#searchTextField").write("hyperten")
                .sleep(800).moveBy(10, 80).clickOn(MouseButton.PRIMARY)
                .clickOn("#goButton");
        assertEquals("HP:0000822", controller.getSelectedTerm().getValue().getId().toString());
    }

    @Test
    public void testPathExists() throws Exception {
        TermId root = ontology.getRootTermId();
        TermId query = new TermId(new TermPrefix("HP"), "0031797");
        Set<TermId> ancessters = OntologyAlgorithm.getAncestorTerms(ontology, query, false);
        assertTrue(!ancessters.isEmpty());
        TermId ancesstor = ancessters.iterator().next();
        //System.out.println(ancesstor);
        //System.out.println(query);
        assertTrue(OntologyAlgorithm.existsPath(ontology, query, ancesstor));
        assertTrue(OntologyAlgorithm.existsPath(ontology, query, root));
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
    private static Ontology ontology() throws IOException, PhenolException {
        HpOboParser parser = new HpOboParser(new File(oboPath));
        return parser.parse();
    }
}