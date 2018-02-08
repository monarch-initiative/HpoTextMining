package com.github.monarchinitiative.hpotextmining.gui.controllers;


import com.google.inject.Injector;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.util.ResourceBundle;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests of {@link com.github.monarchinitiative.hpotextmining.gui.controllers.Configure} class.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.1.0
 * @since 0.1
 */
@Ignore
public class ConfigureTest extends ApplicationTest {

    private static final Injector INJECTOR = GuiceSuiteBase.getInjector();
    private static final ResourceBundle BUNDLE = GuiceSuiteBase.getBundle();

    /**
     * Tested controller.
     */
    private Configure controller;


    /**
     * Test input to text area.
     *
     * @throws Exception bla
     */
    @Test
    public void testInputToTextArea() throws Exception {
        final String[] output = {""};
        controller.setQueryHook(text -> output[0] = text);

        assertThat(output[0], is(""));
        clickOn("#contentTextArea").write("Hey ya!");
        assertThat(output[0], is(""));
        clickOn("#analyzeButton");
        assertThat(output[0], is("Hey ya!"));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage stage) throws Exception {
        controller = INJECTOR.getInstance(Configure.class);
        Parent root = FXMLLoader.load(Configure.class.getResource("Configure.fxml"), BUNDLE,
                new JavaFXBuilderFactory(), INJECTOR::getInstance);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}