package org.monarchinitiative.hpotextmining;

import javafx.stage.Stage;
import org.monarchinitiative.hpotextmining.application.ApplicationConfig;
import org.monarchinitiative.hpotextmining.application.ScreensConfig;
import org.monarchinitiative.hpotextmining.model.DataBucket;
import org.monarchinitiative.hpotextmining.model.SimpleTextMiningResult;
import org.monarchinitiative.hpotextmining.model.TextMiningResult;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Optional;

/**
 * Driver class for Text-mining analysis. See {@link Main} for information how to plug the analysis into existing
 * Java FX program.
 * Created by Daniel Danis on 6/19/17.
 */
public class TextMiningAnalysis {

//    private static final Logger log = LogManager.getLogger();

    private static final String DIALOG_TITLE = "Text-mining analysis";

    /**
     * Run the text-mining analysis (and have fun).
     *
     * @param stage instance of {@link Stage} to be used as owner window for text-mining analysis pop-up windows.
     * @return Optional of {@link TextMiningResult}.
     */
    public static Optional<TextMiningResult> run(Stage stage) {

        ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        ctx.registerShutdownHook();

        final ScreensConfig screensConfig = ctx.getBean(ScreensConfig.class);
        screensConfig.setOwner(stage);
        stage.setTitle(DIALOG_TITLE);

        final DataBucket bucket = ctx.getBean(DataBucket.class);

        // show window where the curator provides data for text-mining analysis.
        screensConfig.configureDialog().showAndWait();
        if (bucket.isCancelled()) {
            return Optional.empty();
        }
        // show window where the curator selects the correctly identified HPO terms.
        screensConfig.presentDialog().showAndWait();
        if (bucket.isCancelled()) {
            return Optional.empty();
        }

        return Optional.of(new SimpleTextMiningResult(bucket.getPMID(), bucket.getApprovedYesTerms(), bucket.getApprovedNotTerms()));
    }

}
