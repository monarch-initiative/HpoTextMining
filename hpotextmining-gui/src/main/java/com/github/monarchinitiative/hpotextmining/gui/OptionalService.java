package com.github.monarchinitiative.hpotextmining.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import ontologizer.ontology.Ontology;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version X.Y.Z
 * @since X.Y
 */
public final class OptionalService {

    private final ObjectProperty<Ontology> ontology = new SimpleObjectProperty<>(this, "ontology", null);


    public Ontology getOntology() {
        return ontology.get();
    }


    public void setOntology(Ontology ontology) {
        this.ontology.set(ontology);
    }


    public ObjectProperty<Ontology> ontologyProperty() {
        return ontology;
    }

}
