package org.monarchinitiative;

import javafx.application.Application;
import javafx.stage.Stage;
import org.monarchinitiative.model.TextMiningResult;

import java.util.Optional;

/**
 * Example usage of {@link TextMiningAnalysis} in JavaFX framework.
 * Created by Daniel Danis on 6/20/17.
 */
public class Example extends Application {

    /**
     * Example usage.
     * @param stage {@link Stage} to be used as owner of text-mining analysis's pop-ups.
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {

        Optional<TextMiningResult> result = TextMiningAnalysis.run(stage);

        result.ifPresent(System.err::println);
    }
}
