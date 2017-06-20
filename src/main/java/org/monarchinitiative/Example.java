package org.monarchinitiative;

import javafx.application.Application;
import javafx.stage.Stage;
import org.monarchinitiative.model.Term;
import org.monarchinitiative.model.TextMiningResult;

import java.util.Optional;
import java.util.Set;

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

        Optional<TextMiningResult> textMiningResult = TextMiningAnalysis.run(stage);
        if (textMiningResult.isPresent()) {
            // data container with results
            TextMiningResult result = textMiningResult.get();

            // set of YES terms approved by the curator
            Set<Term> yesTerms = result.getYesTerms();

            // set of NOT terms approved by the curator
            Set<Term> notTerms = result.getNotTerms();

            // PMID of the publication
            String pmid = result.getPMID();

            System.out.println("YES terms: " + yesTerms);
            System.out.println("NOT terms: " + notTerms);
            System.out.println("PMID: " + pmid);

        }
    }
}
