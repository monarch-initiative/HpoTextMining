package org.monarchinitiative.hpotextmining.gui.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.hpotextmining.core.miners.MinedTerm;
import org.monarchinitiative.hpotextmining.core.miners.SimpleMinedTerm;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.*;

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
     * This variable is being set when testing {@link Present}'s <code>focusToTermHook</code>.
     */
    private TermId focusedOnResult;

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

        terms = new ArrayList<>();

        // 50-58 myopathy
        String myopathyTermIdString = "HP:0003198";
        Term myopathyTerm = ontology.getTermMap().get(TermId.of(myopathyTermIdString));
        MinedTerm myopathyMinedTerm = new SimpleMinedTerm(50, 58, myopathyTermIdString, true);
        terms.add(new Main.PhenotypeTerm(myopathyTerm, myopathyMinedTerm));

        // 75-88 visceromegaly
        String visceromegalyTermIdString = "HP:0003271";
        Term visceromegalyTerm = ontology.getTermMap().get(TermId.of(visceromegalyTermIdString));
        MinedTerm visceromegalyMinedTerm = new SimpleMinedTerm(75, 88, visceromegalyTermIdString, true);
        terms.add(new Main.PhenotypeTerm(visceromegalyTerm, visceromegalyMinedTerm));

        // 114-123 alcoholism
        String alcoholismTermIdString = "HP:0030955";
        Term alcoholismTerm = ontology.getTermMap().get(TermId.of(alcoholismTermIdString));
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
        yesCheckBoxes.sort(Comparator.comparing(l -> ((Main.PhenotypeTerm) l.getUserData()).getTerm().getId().getValue()));

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
    public void addOnePresentTerm() throws Exception {
        Platform.runLater(() -> controller.setResults(terms, payload));
        sleep(LOADING_TIMEOUT);

        // list of 'present' checkboxes
        final List<Node> yesCheckBoxes = new ArrayList<>(((VBox) lookup("#yesTermsVBox").query()).getChildren());
        assertThat(yesCheckBoxes.size(), is(2));
        yesCheckBoxes.sort(Comparator.comparing(l -> ((Main.PhenotypeTerm) l.getUserData()).getTerm().getId().getValue()));

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
    public void addOneNonpresentTerm() throws Exception {
        Platform.runLater(() -> controller.setResults(terms, payload));
        sleep(LOADING_TIMEOUT);
        // list of 'present' checkboxes
        final List<Node> yesCheckBoxes = new ArrayList<>(((VBox) lookup("#yesTermsVBox").query()).getChildren());
        assertThat(yesCheckBoxes.size(), is(2));
        yesCheckBoxes.sort(Comparator.comparing(l -> ((Main.PhenotypeTerm) l.getUserData()).getTerm().getId().getValue()));

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
     * There are two present terms and one nonpresent terms at the beginning of this test. From the present terms, the
     * second term is selected. The nonpresent term is also selected.
     * <p>
     * After dragging the non-selected term we want to retain the selection state for both term groups. The selection state
     * should be also retained after dragging the selected term.
     */
    @Test
    public void dragPresentTermToNonpresentTerms() throws Exception  {
        Platform.runLater(() -> controller.setResults(terms, payload));
        sleep(LOADING_TIMEOUT);
        // list of 'present' checkboxes
        List<Node> yesCheckBoxes = new ArrayList<>(((VBox) lookup("#yesTermsVBox").query()).getChildren());

        assertThat(yesCheckBoxes.size(), is(2));
        yesCheckBoxes.sort(Comparator.comparing(l -> ((Main.PhenotypeTerm) l.getUserData()).getTerm().getId().getValue()));
        // list of 'not present' checkboxes
        List<Node> notCheckBoxes = new ArrayList<>(((VBox) lookup("#notTermsVBox").query()).getChildren());
        assertThat(notCheckBoxes.size(), is(1));
        clickOn(yesCheckBoxes.get(1));
        clickOn(notCheckBoxes.get(0));
        assertThat(((CheckBox) yesCheckBoxes.get(0)).isSelected(), is(false));
        assertThat(((CheckBox) yesCheckBoxes.get(1)).isSelected(), is(true));
        assertThat(((CheckBox) notCheckBoxes.get(0)).isSelected(), is(true));

        // drag the unchecked term representing myopathy to nonpresent terms
        drag(yesCheckBoxes.get(0), MouseButton.PRIMARY)
                .moveTo("#notTermScrollPane")
                .release(MouseButton.PRIMARY);

        Main.PhenotypeTerm visceromegaly = terms.get(1);
        Main.PhenotypeTerm alcoholism = terms.get(2);
        Set<Main.PhenotypeTerm> approvedTerms = controller.getApprovedTerms();
        assertThat(approvedTerms, hasItems(visceromegaly, alcoholism));
        assertThat(approvedTerms.size(), is(2));

        // drag the checked term representing visceromegaly to nonpresent terms
        yesCheckBoxes = new ArrayList<>(((VBox) lookup("#yesTermsVBox").query()).getChildren());
        drag(yesCheckBoxes.get(0), MouseButton.PRIMARY)
                .moveTo("#notTermScrollPane")
                .release(MouseButton.PRIMARY);

        visceromegaly = new Main.PhenotypeTerm(visceromegaly, false); // after dragging the term should be not present
        approvedTerms = controller.getApprovedTerms();
        assertThat(approvedTerms, hasItems(visceromegaly, alcoholism));
        assertThat(approvedTerms.size(), is(2));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage stage) throws Exception {
        controller = new Present(signal -> signalResult = signal, focusedOn -> focusedOnResult = focusedOn);
        FXMLLoader loader = new FXMLLoader(Configure.class.getResource("Present.fxml"));
        loader.setControllerFactory(clazz -> controller);
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }
}