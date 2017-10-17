package org.monarchinitiative.hpotextmining.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ComparisonChain;
import ontologizer.ontology.Term;

import java.util.Comparator;

/**
 * This class is a POJO containing attributes of HPO terms.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.1.0
 * @since 0.1
 */
public class PhenotypeTerm {

    private String hpoId, name, definition;

    private boolean present;


    @JsonCreator
    public PhenotypeTerm(
            @JsonProperty("hpoId") String hpoId,
            @JsonProperty("name") String name,
            @JsonProperty("definition") String definition,
            @JsonProperty("present") boolean present) {
        this.hpoId = hpoId;
        this.name = name;
        this.definition = definition;
        this.present = present;
    }


    public PhenotypeTerm(Term term, boolean present) {
        this.hpoId = term.getIDAsString();
        this.name = term.getName().toString();
        this.definition = (term.getDefinition() == null) ? "" : term.getDefinition().toString();
        this.present = present;
    }


    @JsonGetter
    public boolean isPresent() {
        return present;
    }


    public void setPresent(boolean present) {
        this.present = present;
    }


    @JsonGetter
    public String getHpoId() {
        return hpoId;
    }


    public void setHpoId(String hpoId) {
        this.hpoId = hpoId;
    }


    @JsonGetter
    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    @JsonGetter
    public String getDefinition() {
        return definition;
    }


    public void setDefinition(String definition) {
        this.definition = definition;
    }


    @Override
    public int hashCode() {
        int result = getHpoId().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + (getDefinition() != null ? getDefinition().hashCode() : 0);
        result = 31 * result + (isPresent() ? 1 : 0);
        return result;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PhenotypeTerm that = (PhenotypeTerm) o;

        if (present != that.present) return false;
        if (!hpoId.equals(that.hpoId)) return false;
        if (!name.equals(that.name)) return false;
        return definition.equals(that.definition);
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PhenotypeTerm{");
        sb.append("hpoId='").append(hpoId).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", definition='").append(definition).append('\'');
        sb.append(", present=").append(present);
        sb.append('}');
        return sb.toString();
    }


    /**
     * Comparator for sorting terms by their IDs.
     *
     * @return {@link Comparator} of {@link PhenotypeTerm} objects.
     */
    public static Comparator<PhenotypeTerm> comparatorByHpoID() {
        return (l, r) -> ComparisonChain.start().compare(l.getHpoId(), r.getHpoId()).result();
    }
}
