package com.github.monarchinitiative.hpotextmining.gui;

import com.github.monarchinitiative.hpotextmining.gui.controllers.Main;
import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.1
 * @since 0.2
 */
public final class Play extends Application {

    private Injector injector;


    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(final Stage window) throws Exception {
        injector = Guice.createInjector(new HpoTextMiningModule(window));
        ResourceBundle bundle = injector.getInstance(ResourceBundle.class);
        Parent rootNode = FXMLLoader.load(Main.class.getResource("Main.fxml"), bundle, new JavaFXBuilderFactory(),
                injector::getInstance);
        Image image = new Image(Play.class.getResourceAsStream("/img/icon.png"));
        window.getIcons().add(image);
        window.setTitle(bundle.getString("play.app.title"));
        window.setScene(new Scene(rootNode));
        window.show();
    }


    @Override
    public void stop() throws Exception {
        super.stop();
        injector.getInstance(ExecutorService.class).shutdown();
    }
}
