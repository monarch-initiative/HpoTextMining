package org.monarchinitiative.hpotextmining.demo;

import javafx.application.Application;
import javafx.stage.Stage;
import org.monarchinitiative.hpotextmining.HPOTextMining;
import org.monarchinitiative.hpotextmining.TextMiningResult;
import org.monarchinitiative.hpotextmining.model.PhenotypeTerm;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.stream.Collectors;

/**
 * TODO - write docs
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.1.0
 * @since 0.1
 */
public class Main extends Application {

    private ConfigurableApplicationContext ctx;


    /**
     * Example usage.
     *
     * @param stage {@link Stage} object to be used as owner of text-mining analysis's pop-ups. E.g. the primaryStage
     *              created in FX Application subclass created by JavaFX.
     * @throws Exception just in case
     */
    @Override
    public void start(Stage stage) throws Exception {
        ctx = new AnnotationConfigApplicationContext(ApplicationConfig.class);

        HPOTextMining hpoTextMining = ctx.getBean(HPOTextMining.class);
        TextMiningResult result = hpoTextMining.runAnalysis();
        System.err.println(
                String.format("Approved terms: %s", result.getTerms()
                        .stream()
                        .map(PhenotypeTerm::toString)
                        .collect(Collectors.joining(","))));
        System.err.println(String.format("PMID: %s", result.getPmid()));
    }


    @Override
    public void stop() throws Exception {
        if (ctx != null)
            ctx.stop();
    }


    public static void main(String[] args) {
        launch(args);
    }

}
