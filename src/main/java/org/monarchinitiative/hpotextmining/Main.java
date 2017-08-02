package org.monarchinitiative.hpotextmining;

import javafx.application.Application;
import javafx.stage.Stage;
import org.monarchinitiative.hpotextmining.model.PhenotypeTerm;
import org.monarchinitiative.hpotextmining.model.TextMiningResult;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Example usage of {@link TextMiningAnalysis} plugin in JavaFX application. <p> Please include following code in your
 * JavaFX application in order to perform text mining analysis:
 * <pre>
 * {@code
 *
 * String pmid = "12345"                            // not mandatory
 * String hpoPath = /path/to/HP.obo                 // path to HP.obo file
 * Set<PhenotypeTerm> terms = new HashSet<>();      // set of PhenotypeTerms already present in the model
 * String textMiningUrl = http://example.com        // url of the text-mining server
 *
 * TextMiningAnalysis analysis = new TextMiningAnalysis(hpoPath, pmid, terms, textMiningUrl);
 * Optional<TextMiningResult> textMiningResult = analysis.run(stage);
 * textMiningResult.ifPresent(result -> {
 *
 *     // set of YES terms approved by the curator
 *     Set<PhenotypeTerm> yesTerms = result.getYesTerms();
 *
 *     // set of NOT terms approved by the curator
 *     Set<PhenotypeTerm> notTerms = result.getNotTerms();
 *
 *     // PMID of the publication
 *     String pmid = result.getPMID();
 *
 *     System.out.println("PMID: " + pmid);
 *     System.out.println("YES terms: " + yesTerms);
 *     System.out.println("NOT terms: " + notTerms);
 * });
 *
 *     </pre>
 * </p> Created by Daniel Danis on 8/1/17.
 */
public class Main extends Application {

    private String pmid = "12345";

    private String hpoPath = "/home/ielis/ielis/HpoTextMining/src/test/resources/HP.obo";

    private Set<PhenotypeTerm> terms = new HashSet<>();

    private String textMiningUrl = "http://phenotyper.monarchinitiative.org:5678/cr/annotate";

    /**
     * Example usage.
     *
     * @param stage {@link Stage} object to be used as owner of text-mining analysis's pop-ups. E.g. the primaryStage
     *              created in FX Application subclass created by JavaFX.
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {

        TextMiningAnalysis analysis = new TextMiningAnalysis(hpoPath, pmid, terms, textMiningUrl);
        Optional<TextMiningResult> textMiningResult = analysis.run(stage);
        textMiningResult.ifPresent(result -> {

            // set of YES terms approved by the curator
            Set<PhenotypeTerm> yesTerms = result.getYesTerms();

            // set of NOT terms approved by the curator
            Set<PhenotypeTerm> notTerms = result.getNotTerms();

            // PMID of the publication
            String pmid = result.getPMID();

            System.out.println("PMID: " + pmid);
            System.out.println("YES terms: " + yesTerms);
            System.out.println("NOT terms: " + notTerms);
        });
    }
}
