package org.monarchinitiative.hpotextmining;

import javafx.application.Application;
import javafx.stage.Stage;
import org.monarchinitiative.hpotextmining.model.Term;
import org.monarchinitiative.hpotextmining.model.TextMiningResult;

import java.util.Optional;
import java.util.Set;

/**
 * Example usage of {@link TextMiningAnalysis} plugin in JavaFX application.
 * <p>
 * Please include following code in your JavaFX application in order to perform text mining analysis:
 * <pre>
 * {@code
 *
 * Optional<TextMiningResult> textMiningResult = TextMiningAnalysis.run(stage);
 *
 * if (textMiningResult.isPresent()) { // user haven't cancelled the analysis
 *    // data container with results
 *    TextMiningResult result = textMiningResult.get();
 *
 *    // set of YES terms approved by the curator
 *    Set<Term> yesTerms = result.getYesTerms();
 *
 *    // set of NOT terms approved by the curator
 *    Set<Term> notTerms = result.getNotTerms();
 *
 *    // PMID of the publication
 *    String pmid = result.getPMID();
  *    }
 * }
 *
 *     </pre>
 * </p>
 * Created by Daniel Danis on 6/20/17.
 */
public class Main extends Application {

    /**
     * Example usage.
     *
     * @param stage {@link Stage} object to be used as owner of text-mining analysis's pop-ups. E.g. the
     *                           primaryStage created in FX Application subclass created by JavaFX.
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
