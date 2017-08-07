package org.monarchinitiative.hpotextmining.application;

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
import org.monarchinitiative.hpotextmining.io.AskServer;
import org.monarchinitiative.hpotextmining.model.HTMSignal;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 * Controller for the first Scene presented to curator where the text to be mined for HPO terms is submitted along with
 * PMID of the publication. Created by Daniel Danis on 6/19/17.
 */
public class ConfigureController implements Initializable {

    private static final Logger log = LogManager.getLogger();

    private final URL textMiningServer;

    private Consumer<HTMSignal> signal;

    private StringProperty pmid = new SimpleStringProperty(this, "pmid");

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

    public ConfigureController(URL textMiningServer, String pmid) {
        this.textMiningServer = textMiningServer;
        this.pmid.set((pmid == null) ? "" : pmid);
    }

    public void setSignal(Consumer<HTMSignal> signal) {
        this.signal = signal;
    }

    String getJsonResponse() {
        return jsonResponse;
    }

    String getText() {
        return formatUserInput();
    }

    String getPmid() {
        return pmid.get();
    }

    /**
     * Run analysis task. Configure behavior of the task and run in separate thread.
     */
    @FXML
    void analyzeButtonClicked() {

        task = new AskServer(textMiningServer);

        task.setQuery(formatUserInput());

        task.setOnSucceeded(e -> {
            try {
                jsonResponse = task.get();
            } catch (InterruptedException | ExecutionException ex) {
                log.warn(ex.getMessage());
                signal.accept(HTMSignal.FAILED);
            }
            signal.accept(HTMSignal.DONE); // results are ready
        });

        task.setOnFailed(e -> {
            log.warn("Text mining analysis failed. " + e.getSource().getMessage());
            signal.accept(HTMSignal.FAILED);
        });

        task.setOnCancelled(e -> signal.accept(HTMSignal.CANCELLED));
        Thread askThread = new Thread(task, "AskThread");
        askThread.setDaemon(true);
        askThread.start();
    }

    /**
     * Process text submitted by user. E.g. - remove hyphens at the end of line
     *
     * @return reformatted text String ready to be sent to server.
     */
    private String formatUserInput() {
        return contentTextArea.getText()
                .replace("-\n", "");
    }


    /**
     * @inheritDocs
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
