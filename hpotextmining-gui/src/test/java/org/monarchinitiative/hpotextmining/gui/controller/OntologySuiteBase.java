package org.monarchinitiative.hpotextmining.gui.controller;

import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;

/**
 * This test suite exists in order to parse the Ontology OBO file only once.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.1
 * @since 0.2
 */

class OntologySuiteBase {

    private static final Ontology ontology;

    static {
        ontology = OntologyLoader.loadOntology(OntologySuiteBase.class.getResourceAsStream("/HP.obo"));
    }

    public static Ontology getOntology() {
        return ontology;
    }
}
