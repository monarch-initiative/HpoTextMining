package org.monarchinitiative.hpotextmining.application;

import ontologizer.io.obo.OBOParser;
import ontologizer.io.obo.OBOParserException;
import ontologizer.io.obo.OBOParserFileInput;
import ontologizer.ontology.Ontology;
import ontologizer.ontology.TermContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class contains Beans that are required to run HpoTextMining analysis as standalone application.
 */
@Configuration
@Import(HPOAnalysisScreenConfig.class)
public class HPOAnalysisConfig {

    private static final Logger log = LogManager.getLogger();

    @Autowired
    private Environment env;

    @Bean
    public Ontology ontology() throws IOException, OBOParserException {
        OBOParser parser = new OBOParser(new OBOParserFileInput(env.getProperty("hp.obo.path")), OBOParser
                .PARSE_DEFINITIONS);
        String result = parser.doParse();
        log.info(String.format("HPO file parse result: %s", result));
        TermContainer termContainer = new TermContainer(parser.getTermMap(), parser.getFormatVersion(), parser
                .getDate());
        return Ontology.create(termContainer);
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(2,
                runnable -> {
                    Thread thread = new Thread(runnable);
                    thread.setDaemon(true);
                    return thread;
                });
    }
}
