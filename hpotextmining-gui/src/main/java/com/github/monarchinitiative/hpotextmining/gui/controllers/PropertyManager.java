package com.github.monarchinitiative.hpotextmining.gui.controllers;

import com.github.monarchinitiative.hpotextmining.gui.HpoTextMiningModule;
import com.github.monarchinitiative.hpotextmining.gui.io.DownloadTask;
import com.github.monarchinitiative.hpotextmining.gui.resources.OptionalResources;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ontologizer.io.obo.OBOParser;
import ontologizer.io.obo.OBOParserException;
import ontologizer.io.obo.OBOParserFileInput;
import ontologizer.ontology.Ontology;
import ontologizer.ontology.TermContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

/**
 * This class is a controller for dialog, where user sets the
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.1
 * @since 0.2
 */
public final class PropertyManager {

    private static final Logger LOGGER = LogManager.getLogger();

    private final OptionalResources optionalResources;

    private final Properties properties;

    private final Stage mainWindow;

    private final ExecutorService executorService;

    @FXML
    public Label ontologyPathLabel;

    @FXML
    public Button downloadOntologyButton;

    @FXML
    public Label taskLabel;

    @FXML
    public ProgressBar taskProgressBar;


    @Inject
    public PropertyManager(OptionalResources optionalResources, Properties properties,
                           @Named("mainWindow") Stage mainWindow, ExecutorService executorService) {
        this.optionalResources = optionalResources;
        this.properties = properties;
        this.mainWindow = mainWindow;
        this.executorService = executorService;
    }


    public void initialize() {
        ontologyPathLabel.setText(properties.getProperty(HpoTextMiningModule.HP_OBO_PROPERTY));
        taskProgressBar.setVisible(false);
    }


    @FXML
    public void chooseOntologyButtonAction() {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        chooser.setTitle("Choose ontology file");
        File where = chooser.showOpenDialog(mainWindow);
        if (where == null)
            return; // User didn't choose anything

        try {
            Ontology ontology = parseOntology(where);
            if (ontology.getRootTerm() == null) {
                optionalResources.setOntology(null);
                ontologyPathLabel.setText("Unset");
            } else {
                properties.setProperty(HpoTextMiningModule.HP_OBO_PROPERTY, where.getPath());
                optionalResources.setOntology(ontology);
                ontologyPathLabel.setText(where.getPath());
            }
        } catch (IOException | OBOParserException e) {
            LOGGER.warn(e);
            properties.setProperty(HpoTextMiningModule.HP_OBO_PROPERTY, null);
            optionalResources.setOntology(null);
        }
    }


    @FXML
    public void downloadOntologyAction() {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        chooser.setTitle("Set path where to download the ontology file");
        File where = chooser.showSaveDialog(mainWindow);
        if (where == null) {
            return;
        }
        String hpoUrlString = properties.getProperty(HpoTextMiningModule.HP_OBO_URL);
        if (hpoUrlString == null) {
            LOGGER.warn("Invalid/null URL that should point to location of HPO ontology file");
            return;
        }

        DownloadTask downloadTask = new DownloadTask(hpoUrlString, where);
        downloadTask.setOnRunning(e -> taskProgressBar.setVisible(true));
        downloadTask.setOnFailed(e -> taskProgressBar.setVisible(false));
        downloadTask.setOnSucceeded(e -> {
            taskProgressBar.setVisible(false);
            try {
                Ontology ontology = parseOntology(where);
                optionalResources.setOntology(ontology);
                properties.setProperty(HpoTextMiningModule.HP_OBO_PROPERTY, where.getAbsolutePath());
                ontologyPathLabel.setText(where.getAbsolutePath());
            } catch (OBOParserException | IOException ex) {
                LOGGER.warn(ex);
                optionalResources.setOntology(null);
                properties.setProperty(HpoTextMiningModule.HP_OBO_PROPERTY, null);
            }
        });
        taskLabel.textProperty().bind(downloadTask.messageProperty());
        taskProgressBar.progressProperty().bind(downloadTask.progressProperty());
        executorService.submit(downloadTask);
    }


    private static Ontology parseOntology(File where) throws IOException, OBOParserException {
        OBOParser parser = new OBOParser(new OBOParserFileInput(where.getPath()),
                OBOParser.PARSE_DEFINITIONS);
        LOGGER.info(parser.doParse());
        TermContainer termContainer = new TermContainer(parser.getTermMap(), parser.getFormatVersion(), parser
                .getDate());
        // this parser will parse even a gibberish file into an empty ontology with null root term
        return Ontology.create(termContainer);
    }
}

