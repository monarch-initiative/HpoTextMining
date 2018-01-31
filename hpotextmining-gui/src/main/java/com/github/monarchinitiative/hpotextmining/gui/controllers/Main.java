package com.github.monarchinitiative.hpotextmining.gui.controllers;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Singleton;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.1
 * @since 0.2
 */
@Singleton
public final class Main {

    private static final Logger LOGGER = LogManager.getLogger();


    public void initialize() {
        LOGGER.info("Created Main");
    }


    public void exitMenuItemAction() {
        Platform.exit();
    }


    public void aboutMenuItemAction() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sorry");
        alert.setHeaderText("Sorry");
        alert.setContentText("About menu not yet implemented");
        alert.show();
    }
}
