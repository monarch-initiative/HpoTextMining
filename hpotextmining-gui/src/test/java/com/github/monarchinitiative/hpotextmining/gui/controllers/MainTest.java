package com.github.monarchinitiative.hpotextmining.gui.controllers;


import com.google.inject.Injector;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.VerticalDirection;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests of {@link Main} class.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.1.0
 * @since 0.1
 */
public class MainTest extends ApplicationTest {

    private static final Injector INJECTOR = GuiceSuiteBase.getInjector();

    private static final ResourceBundle BUNDLE = GuiceSuiteBase.getBundle();

    private static String queryText;

    /**
     * Tested instance
     */
    private Main controller;


    @Test
    public void justForFun() {
        // check adding of present and non-present terms obtained by text-mining
        ((TextArea) lookup("#contentTextArea").query()).setText(queryText);
        clickOn("#analyzeButton").sleep(1, TimeUnit.SECONDS);

        VBox yesTermsBox = lookup("#yesTermsVBox").query();
        clickOn(yesTermsBox.getChildren().get(0))
                .clickOn(yesTermsBox.getChildren().get(3))
                .scroll(21, VerticalDirection.DOWN)
                .clickOn(yesTermsBox.getChildren().get(27));
        VBox notTermsBox = lookup("#notTermsVBox").query();
        clickOn(notTermsBox.getChildren().get(0))
                .clickOn(notTermsBox.getChildren().get(1));
        clickOn("#addTermsButton");
        assertThat(controller.getPhenotypeTerms().size(), is(5));
        assertThat(controller.getPhenotypeTerms(), hasItems(new Main.PhenotypeTerm("HP:0001659", "Aortic regurgitation", "An insufficiency of the aortic valve, leading to regurgitation (backward flow) of blood from the aorta into the left ventricle.", true),
                new Main.PhenotypeTerm("HP:0001771", "Achilles tendon contracture", "A contracture of the Achilles tendon.", true),
                new Main.PhenotypeTerm("HP:0002515", "Waddling gait", "", true),
                new Main.PhenotypeTerm("HP:0001373", "Joint dislocation", "Displacement or malalignment of joints.", false),
                new Main.PhenotypeTerm("HP:0003198", "Myopathy", "A disorder of muscle unrelated to impairment of innervation or " +
                        "neuromuscular junction.", false)));

        // check adding non-present term from ontology tree
        doubleClickOn("#searchTextField").write("hyperten")
                .sleep(300)
                .moveBy(10, 80).clickOn(MouseButton.PRIMARY)
                .clickOn("#goButton")
                .clickOn("#notPresentCheckBox")
                .clickOn("#addButton");
        assertThat(controller.getPhenotypeTerms(), hasItem(
                new Main.PhenotypeTerm("HP:0000822", "Hypertension", "The presence of chronic increased pressure in the systemic arterial system.", false)));

        // check adding present term from ontology tree
        doubleClickOn("#searchTextField").write("hepatosplenomegaly")
                .sleep(300).moveBy(10, 30).clickOn(MouseButton.PRIMARY)
                .clickOn("#goButton").clickOn("#addButton");
        assertThat(controller.getPhenotypeTerms(), hasItem(
                new Main.PhenotypeTerm("HP:0001433", "Hepatosplenomegaly", "Simultaneous enlargement of the liver and spleen.", true)));

    }


    // TODO - test removing terms from the table


    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage stage) throws Exception {
        controller = INJECTOR.getInstance(Main.class);
        Parent root = FXMLLoader.load(Main.class.getResource("Main.fxml"), BUNDLE,
                new JavaFXBuilderFactory(), INJECTOR::getInstance);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    @BeforeClass
    public static void beforeClassSetup() throws Exception {
        BufferedReader queryTextReader = new BufferedReader(
                new InputStreamReader(Main.class.getResourceAsStream("/payload.txt")));
        queryText = queryTextReader.lines().collect(Collectors.joining("\n"));
        queryTextReader.close();
    }

}