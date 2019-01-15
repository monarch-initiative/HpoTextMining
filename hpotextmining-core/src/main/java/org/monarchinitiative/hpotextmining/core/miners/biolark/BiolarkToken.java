package org.monarchinitiative.hpotextmining.core.miners.biolark;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.HashSet;
import java.util.Set;

/**
 * This class is an internal representation of a single HPO term within the HPO text-mining analysis. Text-mining
 * server returns JSON response with a collection of these terms. Each PseudoTerm contains: <ul> <li>HPO
 * id</li> <li>preferred HPO label</li> <li>Set of synonyms for preferred label</li> </ul>
 * <p>
 * Created by Daniel Danis on 6/19/17.
 */
public final class BiolarkToken {

    /* HPO id (uri) of this term. */
    private final String id;

    /* Preferred label for this term */
    private  String label;

    /* Set of strings that are synonyms (alternate labels) of term's label*/
    private final Set<String> synonyms;


    /**
     * Create {@link BiolarkToken} with following content.
     *
     * @param id       HPO id (uri)
     * @param label    Preferred label for this label.
     * @param synonyms Alternate labels for this label.
     */
    @JsonCreator
    public BiolarkToken(
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
     * @return string with HPO id with prefix, e.g. HP:000082.
     */
    public String getId() {
        return id;
    }

    /**
     * A convenient method to return the id formatted as the TermId object
     *
     * @return id as a TermId object
     */
    public TermId getTermId() {
        return TermId.of(this.id);
    }

    /**
     * Get a label (term).
     *
     * @return string with label
     */
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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

        BiolarkToken term = (BiolarkToken) o;

        return getId().equals(term.getId());
    }

    @Override
    public String toString() {
        return "BiolarkToken{" +
                "id='" + id + '\'' +
                ", label='" + label + '\'' +
                ", synonyms='" + String.join(", ", synonyms) + "'" +
                '}';
    }
}
