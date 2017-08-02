package org.monarchinitiative.hpotextmining.application;

import ontologizer.io.obo.OBOParser;
import ontologizer.io.obo.OBOParserException;
import ontologizer.io.obo.OBOParserFileInput;
import ontologizer.ontology.Ontology;
import ontologizer.ontology.TermContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.io.IOException;

@Configuration
@Import(HPOAnalysisScreenConfig.class)
@PropertySource("classpath:/applicationTest.properties")
public class HPOTermsAnalysisConfigTest {

    @Autowired
    private Environment env;

    @Bean
    public Ontology ontology() throws IOException, OBOParserException {
        OBOParser parser = new OBOParser(new OBOParserFileInput(env.getProperty("hp.obo.path")), OBOParser
                .PARSE_DEFINITIONS);
        parser.doParse();
        TermContainer termContainer = new TermContainer(parser.getTermMap(), parser.getFormatVersion(), parser.getDate());
        return Ontology.create(termContainer);
    }

}
