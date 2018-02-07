package com.github.monarchinitiative.hpotextmining.gui.controllers;

import com.github.monarchinitiative.hpotextmining.gui.resources.OptionalResources;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.function.Consumer;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.1
 * @since 0.2
 */
public final class Configure {

    private static final Logger LOGGER = LogManager.getLogger();

    private final OptionalResources optionalResources;

    @FXML
    public TextArea contentTextArea;

    @FXML
    public Button analyzeButton;

    private Consumer<String> queryHook;


    @Inject
    public Configure(OptionalResources optionalResources) {
        this.optionalResources = optionalResources;
    }


    public void initialize() {
        analyzeButton.disableProperty().bind(optionalResources.ontologyProperty().isNull());
        contentTextArea.disableProperty().bind(optionalResources.ontologyProperty().isNull());
    }


    /**
     * This hook will consume the text entered into {@link #contentTextArea}.
     *
     * @param queryHook {@link Consumer} of String with text that will be mined for HPO terms
     */
    void setQueryHook(Consumer<String> queryHook) {
        this.queryHook = queryHook;
    }


    /**
     * Clicking on this Button signalizes that the text entered into {@link #contentTextArea} is ready for
     * text-mining analysis
     */
    @FXML
    public void analyzeButtonClicked() {
        if (queryHook != null) {
            queryHook.accept(
                    contentTextArea.getText()
                            .replace("-" + System.lineSeparator(), "") // remove hyphens from words
                            .replace(System.lineSeparator(), " ") // remove line separators
            );
        } else {
            LOGGER.warn("Hook unset and mining requested");
        }
    }

}
