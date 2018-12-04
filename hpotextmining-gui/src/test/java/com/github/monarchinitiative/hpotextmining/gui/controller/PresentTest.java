package com.github.monarchinitiative.hpotextmining.gui.controller;

import com.github.monarchinitiative.hpotextmining.core.miners.MinedTerm;
import com.github.monarchinitiative.hpotextmining.core.miners.SimpleMinedTerm;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.BeforeClass;
import org.junit.Test;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.framework.junit.ApplicationTest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests of the {@link Present} controller.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @author <a href="mailto:aaron.zhangl@jax.org">Aaron Zhang</a>
 * @version 0.2.0
 * @since 0.2.0
 */
public class PresentTest extends ApplicationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PresentTest.class);

    /**
     * Some statements in this test suite have to to be executed on the JavaFX Application Thread. The testing (main) Thread
     * needs to wait a bit for the JavaFX Application Thread. This variable sets how many milliseconds will the waiting last.
     * It looks like that 20 ms is enough on my machine, however feel free to increase this value if the tests are failing
     * on your machine.
     */
    private static final int LOADING_TIMEOUT = 20;

    private static final Ontology ontology = OntologySuiteBase.getOntology();

    private static List<Main.PhenotypeTerm> terms;

    private static String payload = "Here we present a 13-year-old girl with inherited myopathy associated with visceromegaly. The girl was not suffering from alcoholism.";

    /**
     * Tested instance
     */
    private Present controller;

    /**
     * This variable is being set when testing {@link Present#}
     */
    private Main.Signal signalResult;

    /**
     * This variable is being set when testing {@link Present#focusToTermHook}.
     */
    private TermId focusedOnResult;

    @BeforeClass
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

        terms = new ArrayList<>();

        // 50-58 myopathy
        String myopathyTermIdString = "HP:0003198";
        Term myopathyTerm = ontology.getTermMap().get(TermId.constructWithPrefix(myopathyTermIdString));
        MinedTerm myopathyMinedTerm = new SimpleMinedTerm(50, 58, myopathyTermIdString, true);
        terms.add(new Main.PhenotypeTerm(myopathyTerm, myopathyMinedTerm));

        // 75-88 visceromegaly
        String collagenopathyTermIdString = "HP:0003271";
        Term collagenopathyTerm = ontology.getTermMap().get(TermId.constructWithPrefix(collagenopathyTermIdString));
        MinedTerm collagenopathyMinedTerm = new SimpleMinedTerm(75, 88, collagenopathyTermIdString, true);
        terms.add(new Main.PhenotypeTerm(collagenopathyTerm, collagenopathyMinedTerm));

        // 114-123 visceromegaly
        String alcoholismTermIdString = "HP:0030955";
        Term alcoholismTerm = ontology.getTermMap().get(TermId.constructWithPrefix(alcoholismTermIdString));
        MinedTerm alcoholismMinedTerm = new SimpleMinedTerm(122, 132, alcoholismTermIdString, false);
        terms.add(new Main.PhenotypeTerm(alcoholismTerm, alcoholismMinedTerm));
    }


    @Test
    public void addOnePresentAndOneNonpresentTerm() throws Exception {
        Platform.runLater(() -> controller.setResults(terms, payload));
        sleep(LOADING_TIMEOUT);

        // list of 'present' checkboxes
        final List<Node> yesCheckBoxes = new ArrayList<>(((VBox) lookup("#yesTermsVBox").query()).getChildren());
        assertThat(yesCheckBoxes.size(), is(2));
        yesCheckBoxes.sort(Comparator.comparing(l -> ((Main.PhenotypeTerm) l.getUserData()).getTerm().getId().getIdWithPrefix()));

        // list of 'not present' checkboxes
        final List<Node> notCheckBoxes = new ArrayList<>(((VBox) lookup("#notTermsVBox").query()).getChildren());
        assertThat(notCheckBoxes.size(), is(1));

        clickOn(yesCheckBoxes.get(0))
                .clickOn(notCheckBoxes.get(0))
                .clickOn("#addTermsButton");

        final Set<Main.PhenotypeTerm> approvedTerms = controller.getApprovedTerms();
        assertThat(approvedTerms.size(), is(2));
        assertThat(approvedTerms, hasItems(terms.get(0), terms.get(2)));
    }


    @Test
    public void addOnePresentTerm() {
        Platform.runLater(() -> controller.setResults(terms, payload));
        sleep(LOADING_TIMEOUT);

        // list of 'present' checkboxes
        final List<Node> yesCheckBoxes = new ArrayList<>(((VBox) lookup("#yesTermsVBox").query()).getChildren());
        assertThat(yesCheckBoxes.size(), is(2));
        yesCheckBoxes.sort(Comparator.comparing(l -> ((Main.PhenotypeTerm) l.getUserData()).getTerm().getId().getIdWithPrefix()));

        // list of 'not present' checkboxes
        final List<Node> notCheckBoxes = new ArrayList<>(((VBox) lookup("#notTermsVBox").query()).getChildren());
        assertThat(notCheckBoxes.size(), is(1));

        clickOn(yesCheckBoxes.get(1))
                .clickOn("#addTermsButton");

        final Set<Main.PhenotypeTerm> approvedTerms = controller.getApprovedTerms();
        assertThat(approvedTerms.size(), is(1));
        assertThat(approvedTerms, hasItem(terms.get(1)));
    }


    @Test
    public void addOneNonpresentTerm() {
        Platform.runLater(() -> controller.setResults(terms, payload));
        sleep(LOADING_TIMEOUT);
        // list of 'present' checkboxes
        final List<Node> yesCheckBoxes = new ArrayList<>(((VBox) lookup("#yesTermsVBox").query()).getChildren());
        assertThat(yesCheckBoxes.size(), is(2));
        yesCheckBoxes.sort(Comparator.comparing(l -> ((Main.PhenotypeTerm) l.getUserData()).getTerm().getId().getIdWithPrefix()));

        // list of 'not present' checkboxes
        final List<Node> notCheckBoxes = new ArrayList<>(((VBox) lookup("#notTermsVBox").query()).getChildren());
        assertThat(notCheckBoxes.size(), is(1));

        clickOn(notCheckBoxes.get(0))
                .clickOn("#addTermsButton");

        final Set<Main.PhenotypeTerm> approvedTerms = controller.getApprovedTerms();
        assertThat(approvedTerms.size(), is(1));
        assertThat(approvedTerms, hasItem(terms.get(2)));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage stage) throws Exception {
//        LOGGER.info("Loading Present controller @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        controller = new Present(signal -> signalResult = signal, focusedOn -> focusedOnResult = focusedOn);
        FXMLLoader loader = new FXMLLoader(Configure.class.getResource("Present.fxml"));
        loader.setControllerFactory(clazz -> controller);
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }
}