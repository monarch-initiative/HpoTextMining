package com.github.monarchinitiative.hpotextmining.gui.controllers;


import com.google.inject.Injector;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test functionality of {@link OntologyTree} class.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.1.0
 * @since 0.1
 */
@Ignore
public class OntologyTreeTest extends ApplicationTest {

    private static final Injector INJECTOR = GuiceSuiteBase.getInjector();

    private static final ResourceBundle BUNDLE = GuiceSuiteBase.getBundle();

    private OntologyTree controller;


    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage stage) throws Exception {
        controller = INJECTOR.getInstance(OntologyTree.class);
        Parent root = FXMLLoader.load(OntologyTree.class.getResource("OntologyTree.fxml"), BUNDLE,
                new JavaFXBuilderFactory(), INJECTOR::getInstance);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}