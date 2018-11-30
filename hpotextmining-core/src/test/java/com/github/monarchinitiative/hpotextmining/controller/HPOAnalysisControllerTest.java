package com.github.monarchinitiative.hpotextmining.controller;

import com.github.monarchinitiative.hpotextmining.HPOTextMining;
import com.github.monarchinitiative.hpotextmining.model.PhenotypeTerm;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.io.obo.hpo.HpOboParser;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.testfx.framework.junit.ApplicationTest;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Tests of {@link HPOAnalysisController} class.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.1.0
 * @since 0.1
 */
public class HPOAnalysisControllerTest extends ApplicationTest {

    private static final String oboPath = OntologyTreeControllerTest.class.getResource("/HP.obo").getPath();

    private static URL textMiningUrl;

    private static Ontology ontology;

    /**
     * Tested instance
     */
    private HPOAnalysisController controller;


    /**
     * Test lookup and adding of two HPO terms.
     *
     * @throws Exception bla
     */
    @Test
    public void testOntologyTreeIntegration() throws Exception {
        assertThat(controller.getPhenotypeTerms(), hasSize(0));
        doubleClickOn("#searchTextField").write("hepatosplenomegaly")
                .sleep(800).moveBy(10, 30).clickOn(MouseButton.PRIMARY)
                .clickOn("#goButton").clickOn("#addButton");
        assertThat(controller.getPhenotypeTerms(), hasSize(1));
        assertThat(controller.getPhenotypeTerms(), Matchers.hasItem(new PhenotypeTerm("HP:0001433", "Hepatosplenomegaly", "Simultaneous enlargement of the liver and spleen.",
                true)));

        doubleClickOn("#searchTextField").write("hyperten")
                .sleep(800).moveBy(10, 80).clickOn(MouseButton.PRIMARY)
                .clickOn("#goButton").clickOn("#notPresentCheckBox").clickOn("#addButton");
        assertThat(controller.getPhenotypeTerms(), hasSize(2));
        assertTrue(controller.getPhenotypeTerms().contains(new PhenotypeTerm("HP:0000822", "Hypertension", "The " +
                "presence of chronic increased pressure in the systemic arterial system.", false)));
    }


    /**
     * Test that after writing a number into PMID field the model is updated as well.
     *
     * @throws Exception bla
     */
    @Test
    public void testPmid() throws Exception {
        clickOn("#pmidTextField").write("132");
        assertThat(controller.getPmid(), is("132"));
    }

    // TODO - test removing terms from the table


    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage stage) throws Exception {
        controller = new HPOAnalysisController(ontology, textMiningUrl);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TestHPOAnalysisView.fxml"));
        loader.setControllerFactory(param -> controller);
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }


    @BeforeClass
    public static void beforeClassSetUp() throws Exception {
        ontology = ontology();
        textMiningUrl = new URL("http://phenotyper.monarchinitiative.org:5678/cr/annotate");
    }


    /**
     * Parse <em>*.obo</em> file and create {@link Ontology} for {@link HPOTextMining} object.
     *
     * @return {@link Ontology} representing the hierarchy of the ONTOLOGY
     * @throws IOException        if the path to <em>*.obo</em> ONTOLOGY file is incorrect
     * @throws PhenolException if there is a problem with parsing of the ONTOLOGY
     */
    private static Ontology ontology() throws IOException, PhenolException {
        HpOboParser parser = new HpOboParser(new File(oboPath));
        return parser.parse();
    }
}