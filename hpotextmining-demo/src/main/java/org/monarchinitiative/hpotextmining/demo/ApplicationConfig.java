package org.monarchinitiative.hpotextmining.demo;

import javafx.stage.Stage;
import javafx.stage.Window;
import ontologizer.io.obo.OBOParser;
import ontologizer.io.obo.OBOParserException;
import ontologizer.io.obo.OBOParserFileInput;
import ontologizer.ontology.Ontology;
import ontologizer.ontology.TermContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.hpotextmining.HPOTextMining;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.net.URL;

/**
 * Example of Java-based Spring configuration of application that uses <em>hpotextmining-core</em> dialog.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.1.0
 * @since 0.1
 */
@Configuration
@PropertySource("hpotextmining-demo.properties")
public class ApplicationConfig {

    private static final Logger log = LogManager.getLogger();

    @Autowired
    private Environment env;

    // this is null at the moment
    // TODO - figure out how to smuggle the real owner here
    private Stage owner;


    /**
     * Create high-level {@link HPOTextMining} object responsible for running text-mining analysis.
     *
     * @return {@link HPOTextMining} object
     * @throws IOException        in case of I/O errors
     * @throws OBOParserException in case of errors during parsing of <em>*.obo</em> ontology file
     */
    @Bean
    public HPOTextMining hpoTextMining() throws IOException, OBOParserException {
        URL textminingUrl = new URL(env.getProperty("textmining.url"));
        return new HPOTextMining(ontology(), textminingUrl, owner);
    }


    /**
     * Parse <em>*.obo</em> file and create {@link Ontology} for {@link HPOTextMining} object.
     *
     * @return {@link Ontology} representing the hierarchy of the ontology
     * @throws IOException        if the path to <em>*.obo</em> ontology file is incorrect
     * @throws OBOParserException if there is a problem with parsing of the ontology
     */
    private Ontology ontology() throws IOException, OBOParserException {
        String hpoPath = env.getProperty("hpo.path");
        OBOParser parser = new OBOParser(new OBOParserFileInput(hpoPath),
                OBOParser.PARSE_DEFINITIONS);
        String result = parser.doParse();
        log.info(String.format("HPO file parse result: %s", result));
        TermContainer termContainer = new TermContainer(parser.getTermMap(), parser.getFormatVersion(), parser
                .getDate());
        return Ontology.create(termContainer);
    }

}
