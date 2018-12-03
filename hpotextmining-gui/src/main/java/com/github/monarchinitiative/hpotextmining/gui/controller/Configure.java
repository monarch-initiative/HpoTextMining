package com.github.monarchinitiative.hpotextmining.gui.controller;

import com.github.monarchinitiative.hpotextmining.core.miners.MinedTerm;
import com.github.monarchinitiative.hpotextmining.core.miners.TermMiner;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;


/**
 * Controller for the part of the dialog that controls entering of text that will be mined for HPO terms along with PMID
 * of a publication.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @author <a href="mailto:aaron.zhangl@jax.org">Aaron Zhang</a>
 * @version 0.1.0
 * @since 0.1
 */
public class Configure {

    private static final Logger LOGGER = LoggerFactory.getLogger(Configure.class);

    private final TermMiner miner;

    private final ExecutorService executorService;

    private final Consumer<Main.Signal> signal;

    private final Set<MinedTerm> terms = new HashSet<>();

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


    Configure(TermMiner miner, ExecutorService executorService, Consumer<Main.Signal> signal) {
        this.miner = miner;
        this.executorService = executorService;
        this.signal = signal;
    }

    Set<MinedTerm> getTerms() {
        return terms;
    }

    String getQuery() {
        return contentTextArea.getText()
                .replace("-" + System.lineSeparator(), ""); // remove newline characters
    }

    /**
     * Run analysis task. Configure behavior of the task and run in separate thread.
     */
    @FXML
    void analyzeButtonClicked() {
        final String query = getQuery();

        // wrap into the Task so that mining will not cause freezing of the GUI
        Task<Collection<MinedTerm>> task = new Task<Collection<MinedTerm>>() {
            @Override
            protected Collection<MinedTerm> call() throws Exception {
                return miner.doMining(query);
            }
        };

        task.setOnSucceeded(e -> {
            try {
                terms.clear();
                terms.addAll(task.get());
                signal.accept(Main.Signal.DONE); // results are ready, notify top-level controller
            } catch (InterruptedException | ExecutionException ex) {
                LOGGER.warn(ex.getMessage());
                signal.accept(Main.Signal.FAILED);
            }
        });

        task.setOnFailed(e -> {
            LOGGER.warn("Text mining analysis failed. " + e.getSource().getMessage());
            signal.accept(Main.Signal.FAILED);
        });

        task.setOnCancelled(e -> signal.accept(Main.Signal.CANCELLED));

        executorService.submit(task);
    }


    /**
     * Initialize GUI elements after processing by FXMLLoader.
     */
    public void initialize() {
        // no-op
    }

}
