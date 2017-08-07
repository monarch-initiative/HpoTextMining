package org.monarchinitiative.hpotextmining;

import javafx.application.Application;
import javafx.stage.Stage;
import ontologizer.io.obo.OBOParser;
import ontologizer.io.obo.OBOParserException;
import ontologizer.io.obo.OBOParserFileInput;
import ontologizer.ontology.Ontology;
import ontologizer.ontology.TermContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.hpotextmining.model.PhenotypeTerm;
import org.monarchinitiative.hpotextmining.model.TextMiningResult;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
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
 * @Override
 * public void start(Stage stage) throws Exception {
 *     URL url = new URL(textMiningUrl);
 *     TextMiningAnalysis analysis = new TextMiningAnalysis.TextMiningAnalysisBuilder()
 *        .setOntology(ontology()).setURL(url).setPmid(pmid).setPhenotypeTerms(new HashSet<>()).build();
 *     TextMiningResult results = analysis.run(stage);
 *     System.err.println(results);
 * }
 *
 *
 * public Ontology ontology() throws IOException, OBOParserException {
 *     OBOParser parser = new OBOParser(new OBOParserFileInput(hpoPath), OBOParser.PARSE_DEFINITIONS);
 *     String result = parser.doParse();
 *     log.info(String.format("HPO file parse result: %s", result));
 *     TermContainer termContainer = new TermContainer(parser.getTermMap(), parser.getFormatVersion(),parser.getDate());
 *     return Ontology.create(termContainer);
 * }
 * }
 * </pre>
 * </p> Created by Daniel Danis on 8/1/17.
 */
public class Main extends Application {

    private static final Logger log = LogManager.getLogger();

    private String pmid = "12345";

    private static String hpoPath = "/home/ielis/ielis/HpoTextMining/src/test/resources/HP.obo";

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
        URL url = new URL(textMiningUrl);
        TextMiningAnalysis analysis = new TextMiningAnalysis.TextMiningAnalysisBuilder()
                .setOntology(ontology()).setURL(url).setPmid(pmid).setPhenotypeTerms(new HashSet<>()).build();
        TextMiningResult results = analysis.run(stage);
        System.err.println(results);
    }

    private static Ontology ontology() throws IOException, OBOParserException {
        OBOParser parser = new OBOParser(new OBOParserFileInput(hpoPath),
                OBOParser.PARSE_DEFINITIONS);
        String result = parser.doParse();
        log.info(String.format("HPO file parse result: %s", result));
        TermContainer termContainer = new TermContainer(parser.getTermMap(), parser.getFormatVersion(), parser
                .getDate());
        return Ontology.create(termContainer);
    }
}
