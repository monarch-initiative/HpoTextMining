package org.monarchinitiative.hpotextmining.gui.controller;


import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test functionality of {@link OntologyTree} class.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @author <a href="mailto:aaron.zhangl@jax.org">Aaron Zhang</a>
 * @version 0.1.0
 * @since 0.1
 */
@Disabled
public class OntologyTreeTest extends ApplicationTest {

    private static final Ontology ontology = OntologySuiteBase.getOntology();

    private final List<Main.PhenotypeTerm> results = new ArrayList<>(3);

    private OntologyTree controller;


    @BeforeAll
    public static void setUpBefore() throws Exception {
        // for headless GUI testing, set the "not.headless" system property to true or comment out if you want to see the
        // robot in action
        if (!Boolean.getBoolean("not.headless")) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("java.awt.headless", "true");
            System.setProperty("headless.geometry", "1200x760-32");
        }
    }

    @BeforeEach
    public void setUp() throws Exception {
        results.clear();
    }

    @Test
    public void addSingleTerm() {
        clickOn("#searchTextField")
                .write("hepatosplenomegaly")
                .sleep(200)
                .moveBy(0, 25)
                .clickOn(MouseButton.PRIMARY)
                .clickOn("#goButton")
                .clickOn("#addButton");
        assertThat(results.size(), is(1));

        Main.PhenotypeTerm term = results.get(0);
        assertThat(term.getBegin(), is(-1));
        assertThat(term.getEnd(), is(-1));
        assertThat(term.getTerm().id().getValue(), is("HP:0001433"));
        assertThat(term.getTerm().getName(), is("Hepatosplenomegaly"));
        assertThat(term.isPresent(), is(true));
    }


    /**
     * Add two terms, the first is present and the second is unpresent.
     */
    @Test
    public void addTwoTerms() {
        clickOn("#searchTextField")
                .write("pituitary") // the first term
                .sleep(200)
                .moveBy(0, 50)
                .clickOn(MouseButton.PRIMARY)
                .clickOn("#goButton")
                .clickOn("#addButton")
                .doubleClickOn("#searchTextField") // the second term
                .write("methemoglobine")
                .sleep(200)
                .moveBy(0, 20)
                .clickOn(MouseButton.PRIMARY)
                .clickOn("#goButton")
                .clickOn("#notPresentCheckBox")
                .clickOn("#addButton");

        assertThat(results.size(), is(2));

        final Main.PhenotypeTerm pituitary = results.get(0);
        assertThat(pituitary.getBegin(), is(-1));
        assertThat(pituitary.getEnd(), is(-1));
        assertThat(pituitary.getTerm().id().getValue(), is("HP:0011747"));
        assertThat(pituitary.getTerm().getName(), is("Abnormality of the anterior pituitary"));
        assertThat(pituitary.isPresent(), is(true));

        final Main.PhenotypeTerm hemoglobine = results.get(1);
        assertThat(hemoglobine.getBegin(), is(-1));
        assertThat(hemoglobine.getEnd(), is(-1));
        assertThat(hemoglobine.getTerm().id().getValue(), is("HP:0012119"));
        assertThat(hemoglobine.getTerm().getName(), is("Methemoglobinemia"));
        assertThat(hemoglobine.isPresent(), is(false));
    }


    @Override
    public void start(Stage stage) throws Exception {
        controller = new OntologyTree(ontology, results::add);
        FXMLLoader loader = new FXMLLoader(OntologyTree.class.getResource("OntologyTree.fxml"));
        loader.setControllerFactory(clz -> controller);
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }

}