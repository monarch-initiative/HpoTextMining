package com.github.monarchinitiative.hpotextmining.gui.resources;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import ontologizer.ontology.Ontology;

/**
 * The aim of this class is to group the optional resources required for GUI. An optional resource is a resource which
 * may or may not be available during the start of the GUI. If the resource is not available, some functions of GUI
 * should be disabled (e.g. ontology tree view should be disabled, if ontology OBO file has not been downloaded yet).
 * <p>
 * Controllers of GUI that depend on optional resource should create listeners in their <code>initialize</code>
 * methods, such that the listeners will disable controls if the resource is <code>null</code>.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.1
 * @see ResourceValidators
 * @see ResourceValidator
 * @since 0.2
 */
public final class OptionalResources {

    private final ObjectProperty<Ontology> ontology = new SimpleObjectProperty<>(this, "ontology", null);


    public Ontology getOntology() {
        return ontology.get();
    }


    public void setOntology(Ontology ontology) {
        this.ontology.set(ResourceValidators.ontologyResourceValidator().isValid(ontology) ? ontology : null);
    }


    public ObjectProperty<Ontology> ontologyProperty() {
        return ontology;
    }

}
