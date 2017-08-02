package org.monarchinitiative.hpotextmining;

import javafx.stage.Stage;
import org.monarchinitiative.hpotextmining.application.HPOAnalysisConfig;
import org.monarchinitiative.hpotextmining.application.HPOAnalysisController;
import org.monarchinitiative.hpotextmining.application.HPOAnalysisScreenConfig;
import org.monarchinitiative.hpotextmining.model.PhenotypeTerm;
import org.monarchinitiative.hpotextmining.model.SingleTextMiningResult;
import org.monarchinitiative.hpotextmining.model.TextMiningResult;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;
import java.util.Optional;
import java.util.Set;

/**
 * Driver class for Text-mining analysis. See {@link Main} for information how to plug the analysis into existing Java
 * FX program. Created by Daniel Danis on 6/19/17.
 */
public class TextMiningAnalysis {

    private static final String DIALOG_TITLE = "Text-mining analysis";

    private final String hpoPath, pmid, serverUrl;

    private final Set<PhenotypeTerm> knownTerms;

    public TextMiningAnalysis(String hpoPath, String pmid, Set<PhenotypeTerm> knownTerms, String serverUrl) {
        this.knownTerms = knownTerms;
        this.pmid = pmid;
        this.hpoPath = hpoPath;
        this.serverUrl = serverUrl;
    }

    /**
     * Run the text-mining analysis (and have fun).
     *
     * @param stage instance of {@link Stage} to be used as owner window for text-mining analysis pop-up windows.
     * @return Optional of {@link TextMiningResult}.
     */
    public Optional<TextMiningResult> run(Stage stage) {
        stage.setTitle(DIALOG_TITLE);

        File hpoPath = new File(this.hpoPath);
        if (!(hpoPath.exists() && hpoPath.isFile())) {
            return Optional.empty();
        }
        System.setProperty("hp.obo.path", hpoPath.getAbsolutePath());
        System.setProperty("text.mining.url", serverUrl);
        ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(HPOAnalysisConfig.class);
        ctx.registerShutdownHook();

        final HPOAnalysisScreenConfig analysisConfig = ctx.getBean(HPOAnalysisScreenConfig.class);
        final HPOAnalysisController controller = ctx.getBean(HPOAnalysisController.class);

        controller.addPhenotypeTerms(knownTerms);
        controller.setPmid(pmid);

        analysisConfig.hpoAnalysisDialog().showAndWait();
        SingleTextMiningResult result = new SingleTextMiningResult(controller.getPmid(), controller.getPhenotypeTerms());
        return Optional.of(result);
    }
}
