package org.monarchinitiative.hpotextmining.gui.controller;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This test suite exists in order to parse the Ontology OBO file only once.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.1
 * @since 0.2
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({OntologyTreeTest.class, PresentTest.class, HpoTextMiningTest.class})
public class OntologySuiteBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(OntologySuiteBase.class);

    private static Ontology ontology;

    static {
        String ontologyPath = "/HP.obo";
        ontology = OntologyLoader.loadOntology(OntologySuiteBase.class.getResourceAsStream(ontologyPath));
    }

    public static Ontology getOntology() {
        return ontology;
    }
}
