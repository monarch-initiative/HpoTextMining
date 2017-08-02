package org.monarchinitiative.hpotextmining.application;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.hpotextmining.io.AskServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;

/**
 * Controller for the first Scene presented to curator where the text to be mined for HPO terms is submitted along with
 * PMID of the publication. Created by Daniel Danis on 6/19/17.
 */
public class ConfigureController implements Initializable {

    private static final Logger log = LogManager.getLogger();

    private HPOAnalysisScreenConfig screenConfig;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private Environment env;

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

    public ConfigureController(HPOAnalysisScreenConfig screenConfig) {
        this.screenConfig = screenConfig;
    }

    /**
     * Run analysis task. Configure behavior of the task and run in separate thread.
     */
    @FXML
    void analyzeButtonClicked() {
        try {
            task = new AskServer(new URL(env.getProperty("text.mining.url")));
        } catch (MalformedURLException e) {
            log.warn(e);
            e.printStackTrace();
            return;
        }
        task.setQuery(formatUserInput());

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            screenConfig.presentController().setResults(task.getValue(), formatUserInput());
            ObservableList<Node> children = screenConfig.hpoAnalysisController().getTextMiningStackPane().getChildren();
            children.clear();
            children.add(screenConfig.presentDialog());
        }));

        task.setOnFailed(e -> log.warn("Text mining analysis failed. " + e.getSource().getMessage()));
        executorService.submit(task);
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

        pmidTextField.textProperty().bindBidirectional(screenConfig.hpoAnalysisController().pmidProperty());
    }

    String getText() {
        return contentTextArea.getText();
    }

}
