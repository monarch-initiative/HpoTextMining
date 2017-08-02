package org.monarchinitiative.hpotextmining.application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.monarchinitiative.hpotextmining.model.PhenotypeTerm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = HPOTermsAnalysisConfigTest.class)
public class HPOAnalysisControllerTest extends ApplicationTest {

    private static String WIDOWS_PEAK_DEFINITION = "Frontal hairline with bilateral arcs to a low point in the " +
            "midline of the forehead.";

    @Autowired
    private HPOAnalysisController controller;


    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        System.gc();
    }

    /**
     * Utility method for sorting Set with given comparator that returns List.
     *
     * @return
     */
    private static <T> List<T> sortSet(Set<T> terms, Comparator<T> comparator) {
        return terms.stream().sorted(comparator).collect(Collectors.toList());
    }

    /**
     * Read file containing sample data from classpath.
     */
    private static String readFileFromClasspath(URL url) throws URISyntaxException, IOException {
        return Files.readAllLines(Paths.get(url.toURI())).stream().collect(Collectors.joining("\n"));
    }


    /**
     * This is sort of a @Before method that is run before each Test case.
     *
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        // simulate the terms that were in the model and that should be displayed in the HPOAnalysis View.
        controller.addPhenotypeTerms(new HashSet<>(Arrays.asList(
                new PhenotypeTerm("HP:0000349", "Widow's peak", WIDOWS_PEAK_DEFINITION, "6y", "Nonprogressive",
                        "Mild", "Chronic", "Generalized", "Bilateral", true),
                new PhenotypeTerm("HP:0000391", "Thickened helices", "Increased thickness of the helix of the ear" +
                        ".", "10y", "Nonprogressive", "Chronic", "Generalized", "Generalized", "Bilateral",
                        false))));
        controller.setPmid("");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HPOAnalysisView.fxml"));
        loader.setControllerFactory(param -> controller);
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }

    @Test
    @Ignore // should springscreen check thread and load content always using FX thread?
    public void integrationTest() throws Exception {
        TextArea content = lookup("#contentTextArea").query();
        Button analyze = lookup("#analyzeButton").query();
        clickOn(content)
                .write("Bacon ham cholesterol and hypertension are cool but not hepatosplenomegaly")
                .clickOn(analyze);
        Thread.sleep(5000); // allow time for response
        VBox yesBox = lookup("#yesTermsVBox").query();
        List<Node> yesBoxes = ((VBox) lookup("#yesTermsVBox").query()).getChildren();
    }

    /**
     * Test the method {@link HPOAnalysisController#addPhenotypeTerms(Set)}. Add new {@link PhenotypeTerm}.
     *
     * @throws Exception
     */
    @Test
    public void addPhenotypeTerms() throws Exception {
        assertEquals(2, controller.getPhenotypeTerms().size());
        controller.addPhenotypeTerms(new HashSet<>(Collections.singletonList(
                new PhenotypeTerm("HP:0000395", "Prominent antihelix", "The presence of an abnormally prominent " +
                        "antihelix", "1y", "Nonprogressive", "Mild", "Chronic", "Generalized", "Bilateral",
                        false))));
        assertEquals(3, controller.getPhenotypeTerms().size());
    }

    /**
     * Add a new term using autocompletion suggestion box. The term is present in the patient.
     *
     * @throws Exception
     */
    @Test
    public void addPresentTerm() throws Exception {
        TextField addTerm = lookup("#addTermTextField").query();
        Button addButton = lookup("#addTermButton").query();
        clickOn(addTerm).write("hyper").moveBy(-150, 70).clickOn(MouseButton.PRIMARY).clickOn(addButton);
        assertEquals(3, controller.getPhenotypeTerms().size());
        PhenotypeTerm term = sortSet(controller.getPhenotypeTerms(), PhenotypeTerm.comparatorByHpoID()).get(2);
        assertEquals("HP:0008281", term.getHpoId());
        assertEquals("Acute hyperammonemia", term.getName());
        assertTrue(term.isPresent());
        assertTrue(addTerm.getText().equals(""));
    }

    /**
     * Add a new term using autocompletion suggestion box. Select NOT checkbox to add the term as non-present.
     *
     * @throws Exception
     */
    @Test
    public void addNonPresent() throws Exception {
        TextField addTerm = lookup("#addTermTextField").query();
        CheckBox not = lookup("#notObservedCheckBox").query();
        Button addButton = lookup("#addTermButton").query();
        clickOn(addTerm).write("hyper").moveBy(-150, 70);
        clickOn(MouseButton.PRIMARY);
        clickOn(not);
        clickOn(addButton);
        PhenotypeTerm term = sortSet(controller.getPhenotypeTerms(), PhenotypeTerm.comparatorByHpoID()).get(2);
        assertEquals("HP:0008281", term.getHpoId());
        assertEquals("Acute hyperammonemia", term.getName());
        assertFalse(term.isPresent());
        assertFalse(not.isSelected()); // checkbox should be unselected after adding non-present term.
        assertTrue(addTerm.getText().equals(""));
    }

    /**
     * Test that the selected row in a table is removed after clicking on Remove button.
     *
     * @throws Exception
     */
    @Test
    public void removeButtonAction() throws Exception {
        assertEquals(2, controller.getPhenotypeTerms().size());
        TableView<PhenotypeTerm> termTableView = lookup("#hpoTermsTableView").query();
        Button removeButton = lookup("#removeTermButton").query();
        moveTo(termTableView).moveBy(-80, -45).clickOn(MouseButton.PRIMARY).clickOn(removeButton);
        assertEquals(1, controller.getPhenotypeTerms().size());
    }

    /**
     * Test that the terms already present in model will appear in controller's TableView.
     *
     * @throws Exception
     */
    @Test
    public void initialization() throws Exception {
        assertEquals(2, controller.getPhenotypeTerms().size());
        Set<PhenotypeTerm> terms = controller.getPhenotypeTerms();
        List<PhenotypeTerm> sortedTerms = sortSet(terms, PhenotypeTerm.comparatorByHpoID());
        PhenotypeTerm first = sortedTerms.get(0);
        PhenotypeTerm second = sortedTerms.get(1);
        assertEquals("HP:0000349", first.getHpoId());
        assertEquals("HP:0000391", second.getHpoId());
        assertEquals("Widow's peak", first.getName());
        assertEquals("Thickened helices", second.getName());
    }

}