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

/**
 * Tests of the {@link Present}, not yet implemented.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.0
 * @since 0.2.0
 */
@Ignore
public class PresentTest extends ApplicationTest {

    private static final Injector INJECTOR = GuiceSuiteBase.getInjector();

    private static final ResourceBundle BUNDLE = GuiceSuiteBase.getBundle();

    /**
     * Tested instance
     */
    private Present controller;


    @Test
    public void justForFun() throws Exception {
        sleep(3000);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage stage) throws Exception {
        controller = INJECTOR.getInstance(Present.class);
        Parent root = FXMLLoader.load(Present.class.getResource("Present.fxml"), BUNDLE,
                new JavaFXBuilderFactory(), INJECTOR::getInstance);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}