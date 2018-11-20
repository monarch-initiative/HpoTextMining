package com.github.monarchinitiative.hpotextmining.controller;

import com.github.monarchinitiative.hpotextmining.io.AskSciGraphServer;
import com.github.monarchinitiative.hpotextmining.io.AskServer;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.github.monarchinitiative.hpotextmining.io.AskTudorServer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import static com.github.monarchinitiative.hpotextmining.controller.HPOAnalysisController.Signal.*;

/**
 * Controller for the part of the dialog that controls entering of text that will be mined for HPO terms along with PMID
 * of a publication.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.1.0
 * @since 0.1
 */
public class ConfigureController implements Initializable {


    private static final Logger log = LogManager.getLogger();

    private final URL textMiningServer;

    private Consumer<HPOAnalysisController.Signal> signal;

    private StringProperty pmid = new SimpleStringProperty(this, "pmid", "");

    private String jsonResponse;

    /**
     * User will paste query text here.
     */
    @FXML
    private TextArea contentTextArea;

    /**
     * Clicking on this button will start the analysis.
     */
    @FXML
    private Button analyzeButton;

    @FXML
    private TextField pmidTextField;

    private AskServer task;


    public ConfigureController(URL textMiningServer) {
        this.textMiningServer = textMiningServer;
    }


    public void setSignal(Consumer<HPOAnalysisController.Signal> signal) {
        this.signal = signal;
    }


    String getJsonResponse() {
        return jsonResponse;
    }


    String getText() {
        return preprocessInput();
    }


    String getPmid() {
        return pmid.get();
    }


    void setPmid(String pmid) {
        this.pmid.set(pmid);
    }


    /**
     * Run analysis task. Configure behavior of the task and run in separate thread.
     */
    @FXML
    void analyzeButtonClicked() {
        //task = new AskTudorServer(textMiningServer);
        //
        task = new AskSciGraphServer(textMiningServer);

        task.setQuery(preprocessInput());
        //task.setQuery(contentTextArea.getText());

        task.setOnSucceeded(e -> {
            try {
                jsonResponse = task.get();
            } catch (InterruptedException | ExecutionException ex) {
                log.warn(ex.getMessage());
                signal.accept(FAILED);
            }
            signal.accept(DONE); // results are ready, notify top-level controller
        });

        task.setOnFailed(e -> {
            log.warn("server host: " + textMiningServer.getHost());
            log.warn("server path: " + textMiningServer.getPath());
            log.warn("query text: " + contentTextArea.getText());
            log.warn("Text mining analysis failed. " + e.getSource().getMessage());
            signal.accept(FAILED);
        });

        task.setOnCancelled(e -> signal.accept(CANCELLED));
        Thread askThread = new Thread(task, "AskThread");
        askThread.setDaemon(true);
        askThread.start();
    }


    /**
     * Pre-process text submitted by user. E.g. - remove hyphens at the end of line and concatenate the lines.
     *
     * @return reformatted text String ready to be sent to server.
     */
    private String preprocessInput() {
        return contentTextArea.getText()
                .replace("-" + System.lineSeparator(), "");
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // This binding allows to start the analysis only after that all required info has been entered.
        BooleanBinding allSet = Bindings.createBooleanBinding(() -> !pmidTextField.getText().matches("\\d{1,9}") ||
                        contentTextArea.getText().equalsIgnoreCase(""),
                pmidTextField.textProperty(), contentTextArea.textProperty());
        analyzeButton.disableProperty().bind(allSet);

        /* The following removes the annoying spaces that NCBI puts
        * in front of the PMID when you copy it from the webpage. */
        pmidTextField.textProperty().addListener(((observable, oldValue, newValue) ->
                pmidTextField.setText(newValue.trim())));

        pmidTextField.textProperty().bindBidirectional(pmid);
    }

}
