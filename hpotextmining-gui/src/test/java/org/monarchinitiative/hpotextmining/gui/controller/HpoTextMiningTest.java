package org.monarchinitiative.hpotextmining.gui.controller;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.hpotextmining.core.miners.MinedTerm;
import org.monarchinitiative.hpotextmining.core.miners.TermMiner;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.mockito.Mockito;
import org.monarchinitiative.phenol.ontology.data.MinimalOntology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Here we test all the functionality of the text mining widget {@link HpoTextMining}.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.1.0
 * @since 0.1
 */
@Disabled
public class HpoTextMiningTest extends ApplicationTest {

    /**
     * Some statements in this test suite have to to be executed on the JavaFX Application Thread. The testing (main) Thread
     * needs to wait a bit for the JavaFX Application Thread. This variable sets how many milliseconds will the waiting last.
     * It looks like that 20 ms is enough on my machine, however feel free to increase this value if the tests are failing
     * on your machine.
     */
    private static final int LOADING_TIMEOUT = 20;

    private static final MinimalOntology ontology = OntologySuiteBase.getOntology();

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static String payload;

    private static Collection<MinedTerm> terms;

    private HpoTextMining hpoTextMining;

    private final TermMiner miner = Mockito.mock(TermMiner.class);

    @BeforeAll
    public static void setUpBefore() throws Exception {
        // read query text from file
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(HpoTextMiningTest.class.getResourceAsStream("payload.txt")))) {
            payload = reader.lines().collect(Collectors.joining("\n"));
        }

        terms = Set.of(
                MinedTerm.of(1602, 1630, "HP:0001771", true),
                MinedTerm.of(2211, 2233, "HP:0040287", true),
                MinedTerm.of(-1, -1, "HP:0011747", true),
                MinedTerm.of(-1, -1, "HP:0012119", false));

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

    @Test
    public void pasteLargePayloadAndAddTerms() throws Exception {
        Mockito.when(miner.doMining(payload)).thenReturn(terms);

        // insert payload text at first
        TextArea contentTextArea = lookup("#contentTextArea").query();
        Platform.runLater(() -> contentTextArea.setText(payload));
        sleep(LOADING_TIMEOUT)
                .clickOn("#analyzeButton")
                .sleep(LOADING_TIMEOUT);

        VBox yesTermsVBox = lookup("#yesTermsVBox").query();
        assertThat(yesTermsVBox.getChildren().size(), is(30));

        VBox notTermsVBox = lookup("#notTermsVBox").query();
        assertThat(notTermsVBox.getChildren().size(), is(0));

        // add a few terms from the payload
        clickOn(yesTermsVBox.getChildren().get(0))
                .clickOn(yesTermsVBox.getChildren().get(4))
                .clickOn("#addTermsButton");

        // then add two from the ontology tree
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

        Set<Main.PhenotypeTerm> approvedTerms = hpoTextMining.getApprovedTerms();
        assertThat(approvedTerms.size(), is(4));

        assertThat(approvedTerms, hasItem(new Main.PhenotypeTerm(ontology.termForTermId(TermId.of("HP:0001771")).orElseThrow(), 1602, 1630, true)));
        assertThat(approvedTerms, hasItem(new Main.PhenotypeTerm(ontology.termForTermId(TermId.of("HP:0040287")).orElseThrow(), 2211, 2233, true)));
        assertThat(approvedTerms, hasItem(new Main.PhenotypeTerm(ontology.termForTermId(TermId.of("HP:0011747")).orElseThrow(), -1, -1, true)));
        assertThat(approvedTerms, hasItem(new Main.PhenotypeTerm(ontology.termForTermId(TermId.of("HP:0012119")).orElseThrow(), -1, -1, false)));
    }

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);

        hpoTextMining = HpoTextMining.builder()
                .withOntology(ontology)
                .withTermMiner(miner)
                .withExecutorService(executorService)
                .withPhenotypeTerms(Set.of())
                .build();

        stage.setScene(new Scene(hpoTextMining.getMainParent()));
        stage.show();
    }

}
