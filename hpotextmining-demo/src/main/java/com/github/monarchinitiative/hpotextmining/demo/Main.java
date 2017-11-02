package com.github.monarchinitiative.hpotextmining.demo;

import com.github.monarchinitiative.hpotextmining.TextMiningResult;
import com.github.monarchinitiative.hpotextmining.model.PhenotypeTerm;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.github.monarchinitiative.hpotextmining.HPOTextMining;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.stream.Collectors;

/**
 * Example usage of <code>hpotextmining-core</code> module in JavaFX App.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.1.0
 * @since 0.1
 */
public class Main extends Application {

    private static final String PROP_FILE_NAME = "hpotextmining-demo.properties";

    private static final Logger LOGGER = LogManager.getLogger();

    private ConfigurableApplicationContext ctx;


    @Override
    public void init() throws Exception {
        super.init();
        System.setProperty("properties.path", propertiesFilePath().toString());
        System.setProperty("hp.obo.path", hpoPath());
    }


    /**
     * Create {@link HPOTextMining} object with or without Spring and run the analysis.
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
        System.out.println(
                String.format("Approved terms: %s", result.getTerms()
                        .stream()
                        .map(PhenotypeTerm::toString)
                        .collect(Collectors.joining(","))));
        System.out.println(String.format("PMID: %s", result.getPmid()));
    }


    @Override
    public void stop() throws Exception {
        if (ctx != null)
            ctx.stop();
    }


    private static Path propertiesFilePath() {
        String[] possiblePaths =
                {jarFilePath().toString() + File.separator + "classes" + File.separator + PROP_FILE_NAME,
                        jarFilePath().toString() + File.separator + PROP_FILE_NAME};

        for (String possiblePath : possiblePaths) {
            Path p = Paths.get(possiblePath);
            if (Files.exists(p) && Files.isRegularFile(p)) {
                return p;
            }
        }
        LOGGER.error("Unable to find properties file");
        throw new RuntimeException("Unable to find properties file");
    }


    private static Path jarFilePath() {
        CodeSource codeSource = Main.class.getProtectionDomain().getCodeSource();
        try {
            return Paths.get(codeSource.getLocation().toURI()).getParent();
        } catch (URISyntaxException ex) {
            LOGGER.error("Unable to find jar file", ex);
            throw new RuntimeException("Unable to find jar file", ex);
        }
    }


    private static String hpoPath() {
        String oboName = "HP.obo";
        File jarParent = jarFilePath().toFile();
        File hpo = new File(jarParent, File.separator + "target" + File.separator + "classes" + File.separator +
                oboName);
        if (hpo.exists())
            return hpo.getAbsolutePath();

        hpo = new File(jarParent, File.separator + "classes" + File.separator + oboName);
        if (hpo.exists())
            return hpo.getAbsolutePath();

        hpo = new File(jarParent, File.separator + oboName);
        if (hpo.exists())
            return hpo.getAbsolutePath();

        throw new RuntimeException("Unable to find obo file!");
    }


    public static void main(String[] args) {
        launch(args);
    }

}
