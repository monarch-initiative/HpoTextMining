package org.monarchinitiative.hpotextmining.gui.controller;

import org.monarchinitiative.phenol.io.MinimalOntologyLoader;
import org.monarchinitiative.phenol.ontology.data.MinimalOntology;

/**
 * This test suite exists in order to parse the Ontology OBO file only once.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.1
 * @since 0.2
 */

class OntologySuiteBase {

    private static final MinimalOntology ontology;

    static {
        ontology = MinimalOntologyLoader.loadOntology(OntologySuiteBase.class.getResourceAsStream("/hp.json"));
    }

    public static MinimalOntology getOntology() {
        return ontology;
    }
}
