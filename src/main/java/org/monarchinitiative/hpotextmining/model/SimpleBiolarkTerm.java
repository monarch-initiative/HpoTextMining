package org.monarchinitiative.hpotextmining.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is a internal representation of a single HPO term within the HPO text-mining analysis. Text-mining
 * analysis produces a collection of such terms. Each PseudoTerm contains: <ul> <li>HPO id</li> <li>preferred HPO
 * label</li> <li>Set of synonyms for preferred label</li> </ul>
 * <p>
 * Created by Daniel Danis on 6/19/17.
 */
public class SimpleBiolarkTerm {

    /* HPO id (uri) of this term. */
    private final String id;

    /* Preferred label for this term */
    private final String label;

    /* Set of strings that are synonyms (alternate labels) of term's label*/
    private final Set<String> synonyms;


    /**
     * Create {@link SimpleBiolarkTerm} with following content.
     *
     * @param id       HPO id (uri)
     * @param label    Preferred label for this label.
     * @param synonyms Alternate labels for this label.
     */
    @JsonCreator
    public SimpleBiolarkTerm(
            @JsonProperty("uri") String id,
            @JsonProperty("preferredLabel") String label,
            @JsonProperty("synonyms") Set<String> synonyms
    ) {
        this.id = id;
        this.label = label;
        this.synonyms = synonyms;
    }


    /**
     * Get HPO id for this term.
     *
     * @return string with HPO id.
     */
    public String getId() {
        return id;
    }

    /**
     * Get a label (term).
     *
     * @return string with label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Get a copy of the synonyms.
     *
     * @return set containing the synonyms.
     */
    public Set<String> getSynonyms() {
        return new HashSet<>(synonyms);
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleBiolarkTerm term = (SimpleBiolarkTerm) o;

        return getId().equals(term.getId());
    }

    @Override
    public String toString() {
        return "PseudoTerm{" +
                "id='" + id + '\'' +
                ", label='" + label + '\'' +
                ", synonyms='" + synonyms.stream().collect(Collectors.joining(", ")) + "'" +
                '}';
    }
}
