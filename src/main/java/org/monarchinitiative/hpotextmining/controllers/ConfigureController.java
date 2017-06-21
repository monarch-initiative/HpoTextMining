package org.monarchinitiative.hpotextmining.controllers;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.hpotextmining.application.DialogController;
import org.monarchinitiative.hpotextmining.application.FXMLDialog;
import org.monarchinitiative.hpotextmining.application.ScreensConfig;
import org.monarchinitiative.hpotextmining.io.AskServer;
import org.monarchinitiative.hpotextmining.model.DataBucket;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;

/**
 * Controller for the first Scene presented to curator where the text to be mined for HPO terms is submitted along
 * with PMID of the publication.
 * Created by Daniel Danis on 6/19/17.
 */
public class ConfigureController implements DialogController {

    private static final Logger log = LogManager.getLogger();

    private FXMLDialog dialog;

    @Autowired
    private ExecutorService executor;

    @Autowired
    private DataBucket dataBucket;

    @Autowired
    private AskServer task;

    @FXML
    private Button cancelButton;

    @FXML
    private TextArea contentTextArea;

    @FXML
    private Button analyzeButton;

    @FXML
    private TextField pmidTextField;

    /**
     * Abort the analysis.
     */
    @FXML
    void cancelButtonClicked() {
        task.cancel();
    }

    /**
     * Run analysis task. Configure behavior of the task and submit to executor.
     */
    @FXML
    void analyzeButtonClicked() {

        task.setOnCancelled(e -> {
            dialog.close();
            dataBucket.setCancelled(true);
            });

        task.setOnFailed(e -> {
            ScreensConfig.Alerts.showErrorDialog("Error", null, "Sorry, text-mining analysis failed.");
            dataBucket.setCancelled(true);
            dataBucket.clear();
            Platform.runLater(() -> dialog.close()); // enforce closing the dialog on JavaFX application thread
        });

        task.setOnSucceeded(e -> {
            dataBucket.setPMID(getPMID());
            dataBucket.setMinedText(formatUserInput());
            dataBucket.setJsonResult(task.getValue());
            Platform.runLater(() -> dialog.close()); // enforce closing the dialog on JavaFX application thread
        });

        task.setQuery(formatUserInput());
        executor.submit(task);
        cancelButton.setDisable(false);
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
    }

    String getContentText() {
        return contentTextArea.getText();
    }

    String getPMID() {
        return pmidTextField.getText();
    }


    /**
     * @inheritDocs
     * @param dialog The {@link FXMLDialog} instance which represents an independent window.
     */
    @Override
    public void setDialog(FXMLDialog dialog) {
        this.dialog = dialog;
    }

}
