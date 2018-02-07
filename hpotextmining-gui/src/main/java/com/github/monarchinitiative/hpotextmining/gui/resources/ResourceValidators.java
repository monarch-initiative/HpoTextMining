package com.github.monarchinitiative.hpotextmining.gui.resources;

import ontologizer.ontology.Ontology;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * The aim of this class is to provide validators for optional resources as static methods.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.1
 * @see OptionalResources
 * @see ResourceValidator
 * @since 0.2
 */
public final class ResourceValidators {

    private static final Logger LOGGER = LogManager.getLogger();


    /**
     * Validate the {@link Ontology} object to make sure, that it can be used in GUI. Here, the ontology is valid if
     * it is not empty or <code>null</code>.
     *
     * @return <code>true</code> if the {@link Ontology} is not empty or <code>null</code>
     */
    static ResourceValidator<Ontology> ontologyResourceValidator() {
        return ontology -> {
            if (ontology != null && ontology.getRootTerm() != null)
                return true;
            else {
                LOGGER.warn("Ontology is invalid (either null or empty)");
                return false;
            }
        };
    }

}
