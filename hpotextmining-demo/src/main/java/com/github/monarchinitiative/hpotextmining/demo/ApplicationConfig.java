package com.github.monarchinitiative.hpotextmining.demo;

import com.github.monarchinitiative.hpotextmining.HPOTextMining;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.io.obo.hpo.HpOboParser;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

/**
 * Example of Java-based Spring configuration of application that uses <em>hpotextmining-core</em> dialog.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.1.0
 * @since 0.1
 */
@Configuration
@PropertySource("file:${properties.path}")
public class ApplicationConfig {

    private static final Logger LOGGER = LogManager.getLogger();

    private final Environment env;

    // this is null at the moment
    // TODO - figure out how to smuggle the real owner here
    private Stage owner;

    public ApplicationConfig(Environment env) {
        this.env = env;
    }


    /**
     * Create high-level {@link HPOTextMining} object responsible for running text-mining analysis.
     *
     * @return {@link HPOTextMining} object
     */
    @Bean
    public HPOTextMining hpoTextMining(URL textMiningUrl, Ontology ontology) {
        return new HPOTextMining(ontology, textMiningUrl, owner);
    }

    @Bean
    public URL textMiningUrl() throws MalformedURLException {
        return new URL(new URL(env.getProperty("sciGraph.base")), env.getProperty("sciGraph.path"));
    }

    /**
     * Parse <em>*.obo</em> file and create {@link Ontology} for {@link HPOTextMining} object.
     *
     * @return {@link Ontology} representing the hierarchy of the ONTOLOGY
     * @throws IOException     if the path to <em>*.obo</em> ONTOLOGY file is incorrect
     * @throws PhenolException if there is a problem with parsing of the ONTOLOGY
     */
    @Bean
    public Ontology ontology(File ontologyFilePath) throws IOException, PhenolException {
        LOGGER.info("Loading obo from {}", ontologyFilePath);
        HpOboParser parser = new HpOboParser(ontologyFilePath, false);
        return parser.parse();
    }

    @Bean
    public File ontologyFilePath() {
        return new File(Objects.requireNonNull(env.getProperty("hp.obo.path")));
    }

}
