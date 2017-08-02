package org.monarchinitiative.hpotextmining.application;

import com.genestalker.springscreen.core.FXMLDialog;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.*;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.loadui.testfx.GuiTest;
import org.monarchinitiative.hpotextmining.model.BiolarkResult;
import org.monarchinitiative.hpotextmining.model.PhenotypeTerm;
import org.monarchinitiative.hpotextmining.model.SimpleBiolarkTerm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;


/**
 * Tests of {@link PresentController} class. Created by Daniel Danis on 6/19/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = HPOTermsAnalysisConfigTest.class)
public class PresentControllerTest extends ApplicationTest {

    /**
     * Tested instance
     */
    @Autowired
    private PresentController controller;


    /**
     * Read file containing sample data from classpath.
     */
    private static String readFileFromClasspath(URL url) throws URISyntaxException, IOException {
        return Files.readAllLines(Paths.get(url.toURI())).stream().collect(Collectors.joining("\n"));
    }


    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PresentView.fxml"));
        loader.setControllerFactory(param -> controller);
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }

    /**
     * Test that clicking on term in YES section will add it into the set approved terms.
     *
     * @throws Exception
     */
    @Test
    public void select_yes_checkboxes() throws Exception {
        intitializeController();
        List<Node> yesBoxes = ((VBox) lookup("#yesTermsVBox").query()).getChildren();
        clickOn(yesBoxes.get(0)).clickOn(yesBoxes.get(4)).clickOn(yesBoxes.get(6));
        Set<PhenotypeTerm> resultSet = controller.getApprovedTerms();
        assertEquals(3, resultSet.size());
        List<PhenotypeTerm> sortedTerms = resultSet.stream()
                .sorted(PhenotypeTerm.comparatorByHpoID())
                .collect(Collectors.toList());
        PhenotypeTerm first = sortedTerms.get(0);
        assertEquals("HP:0001288", first.getHpoId());
        assertEquals("Gait disturbance", first.getName());
        assertTrue(first.isPresent());

        PhenotypeTerm second = sortedTerms.get(1);
        assertEquals("HP:0001367", second.getHpoId());
        assertEquals("Abnormal joint morphology", second.getName());
        assertTrue(second.isPresent());

        PhenotypeTerm third = sortedTerms.get(2);
        assertEquals("HP:0011855", third.getHpoId());
        assertEquals("Pharyngeal edema", third.getName());
        assertTrue(third.isPresent());
    }

    /**
     * Test that clicking on term in NOT section will add it into the set approved terms.
     *
     * @throws Exception
     */
    @Test
    public void select_not_checkboxes() throws Exception {
        intitializeController();
        List<Node> notBoxes = ((VBox) lookup("#notTermsVBox").query()).getChildren();
        clickOn(notBoxes.get(0));
        Set<PhenotypeTerm> resultSet = controller.getApprovedTerms();
        assertEquals(1, resultSet.size());
        PhenotypeTerm term = resultSet.iterator().next();
        assertEquals("HP:0001433", term.getHpoId());
        assertEquals("Hepatosplenomegaly", term.getName());
        assertFalse(term.isPresent());
    }

    /**
     * Test that standard input data creates 7 yes term checkboxes & 1 not term checkbox.
     *
     * @throws Exception
     */
    @Test
    public void setResults() throws Exception {
        intitializeController();
        List<Node> yesBoxes = ((VBox) lookup("#yesTermsVBox").query()).getChildren();
        assertEquals(7, yesBoxes.size());
        List<Node> notBoxes = ((VBox) lookup("#notTermsVBox").query()).getChildren();
        assertEquals(1, notBoxes.size());
    }

    /**
     * Test {@link PresentController#colorizeHTML(Set, String)} function with input where the terms are not overlapping
     * themselves.
     *
     * @throws Exception
     */
    @Test
    public void colorizeHTML() throws Exception {
        Set<BiolarkResult> resultSet = PresentController.decodePayload(readFileFromClasspath(getClass().getResource
                ("/payload/payload.json")));
        String minedText = readFileFromClasspath(getClass().getResource("/payload/payload.txt"));
        String expected = readFileFromClasspath(getClass().getResource("/payload/payload.html"));
        String actual = controller.colorizeHTML(resultSet, minedText);
        assertEquals(expected, actual);
    }

    /**
     * Test {@link PresentController#colorizeHTML(Set, String)} function with input where the terms are overlapping.
     *
     * @throws Exception
     */
    @Test
    public void colorizeOverlappingHTML() throws Exception {
        Set<BiolarkResult> resultSet = PresentController.decodePayload(readFileFromClasspath(getClass().getResource
                ("/payload/overlapping_payload.json")));
        String minedText = readFileFromClasspath(getClass().getResource("/payload/overlapping_payload.txt"));
        String expected = readFileFromClasspath(getClass().getResource("/payload/overlapping_payload.html"));
        String actual = controller.colorizeHTML(resultSet, minedText);
        assertEquals(expected, actual);
    }

    /**
     * Test functionality of JSON Parser which will convert String JSON response from the server into a Set of {@link
     * BiolarkResult} objects.
     *
     * @throws Exception
     */
    @Test
    public void decodeResultsTest() throws Exception {
        String jsonResponse = readFileFromClasspath(getClass().getResource("/payload/payload.json"));
        Set<BiolarkResult> resultSet = PresentController.decodePayload(jsonResponse);
        List<BiolarkResult> sortedResults = resultSet.stream().sorted(BiolarkResult.compareByStart()).collect(Collectors
                .toList());

        // Set of synonyms are not being used in equals method, empty set is ok.
        SimpleBiolarkTerm first_result = new SimpleBiolarkTerm("HP:0002815", "Abnormality of the knee", new HashSet<>());
        BiolarkResult first = sortedResults.get(0);

        assertEquals(249, first.getStart());
        assertEquals(265, first.getEnd());
        assertEquals(16, first.getLength());
        assertEquals("knee deformities", first.getOriginal_text());
        assertEquals("HPO", first.getSource());
        assertEquals(first_result, first.getTerm());
        assertFalse(first.isNegated());

        SimpleBiolarkTerm seventh_result = new SimpleBiolarkTerm("HP:0001433", "Hepatosplenomegaly", new HashSet<>());
        BiolarkResult seventh = sortedResults.get(6);

        assertEquals(775, seventh.getStart());
        assertEquals(793, seventh.getEnd());
        assertEquals(18, seventh.getLength());
        assertEquals("hepatosplenomegaly", seventh.getOriginal_text());
        assertEquals("HPO", seventh.getSource());
        assertEquals(seventh_result, seventh.getTerm());
        assertTrue(seventh.isNegated());
    }

    /**
     * Populate controller with test data.
     * @throws Exception
     *
     */
    private void intitializeController() throws Exception {
        String json = readFileFromClasspath(getClass().getResource("/payload/payload.json"));
        String query = readFileFromClasspath(getClass().getResource("/payload/payload.txt"));
        Platform.runLater(() -> controller.setResults(json, query));
        Thread.sleep(500); // give JavaFX Thread time to load results
    }

}