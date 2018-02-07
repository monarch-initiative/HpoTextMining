package com.github.monarchinitiative.hpotextmining.gui;

import com.github.monarchinitiative.hpotextmining.gui.controllers.Main;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.1
 * @since 0.2
 */
public final class Play extends Application {

    private static final Logger LOGGER = LogManager.getLogger();

    private Injector injector;


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
        // implement property writing

        // save properties
        Properties properties = injector.getInstance(Properties.class);
        File where = injector.getInstance(Key.get(File.class, Names.named("propertiesFilePath")));
        properties.store(new FileWriter(where), "HPO text mining properties");
        LOGGER.info("Properties saved to {}", where.getAbsolutePath());
    }


    public static void main(String[] args) {
        Application.launch(args);
    }
}
