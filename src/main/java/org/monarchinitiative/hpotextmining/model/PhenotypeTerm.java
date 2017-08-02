package org.monarchinitiative.hpotextmining.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ComparisonChain;
import ontologizer.ontology.Term;

import java.util.Comparator;

/**
 * This class is a POJO containing attributes of HPO terms.
 */
public class PhenotypeTerm {

    private String hpoId, name, definition, onset, progression, severity, temporalPattern, spatialPattern, laterality;

    private boolean present;

    @JsonCreator
    public PhenotypeTerm(
            @JsonProperty("hpoId") String hpoId,
            @JsonProperty("name") String name,
            @JsonProperty("definition") String definition,
            @JsonProperty("onset") String onset,
            @JsonProperty("progression") String progression,
            @JsonProperty("severity") String severity,
            @JsonProperty("temporalPattern") String temporalPattern,
            @JsonProperty("spatialPattern") String spatialPattern,
            @JsonProperty("laterality") String laterality,
            @JsonProperty("present") boolean present) {
        this.hpoId = hpoId;
        this.name = name;
        this.definition = definition;
        this.onset = onset;
        this.progression = progression;
        this.severity = severity;
        this.temporalPattern = temporalPattern;
        this.spatialPattern = spatialPattern;
        this.laterality = laterality;
        this.present = present;
    }

    public PhenotypeTerm(Term term, boolean present) {
        this.hpoId = term.getIDAsString();
        this.name = term.getName().toString();
        this.definition = (term.getDefinition() == null) ? "" : term.getDefinition().toString();
        this.present = present;
    }

    /**
     * Comparator for sorting terms by their IDs.
     *
     * @return {@link Comparator} of {@link PhenotypeTerm} objects.
     */
    public static Comparator<PhenotypeTerm> comparatorByHpoID() {
        return (l, r) -> ComparisonChain.start().compare(l.getHpoId(), r.getHpoId()).result();
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

    @JsonGetter
    public String getOnset() {
        return onset;
    }

    public void setOnset(String onset) {
        this.onset = onset;
    }

    @JsonGetter
    public String getProgression() {
        return progression;
    }

    public void setProgression(String progression) {
        this.progression = progression;
    }

    @JsonGetter
    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    @JsonGetter
    public String getTemporalPattern() {
        return temporalPattern;
    }

    public void setTemporalPattern(String temporalPattern) {
        this.temporalPattern = temporalPattern;
    }

    @JsonGetter
    public String getSpatialPattern() {
        return spatialPattern;
    }

    public void setSpatialPattern(String spatialPattern) {
        this.spatialPattern = spatialPattern;
    }

    @JsonGetter
    public String getLaterality() {
        return laterality;
    }

    public void setLaterality(String laterality) {
        this.laterality = laterality;
    }

    @Override
    public int hashCode() {
        int result = getHpoId().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + (getDefinition() != null ? getDefinition().hashCode() : 0);
        result = 31 * result + (getOnset() != null ? getOnset().hashCode() : 0);
        result = 31 * result + (getProgression() != null ? getProgression().hashCode() : 0);
        result = 31 * result + (getSeverity() != null ? getSeverity().hashCode() : 0);
        result = 31 * result + (getTemporalPattern() != null ? getTemporalPattern().hashCode() : 0);
        result = 31 * result + (getSpatialPattern() != null ? getSpatialPattern().hashCode() : 0);
        result = 31 * result + (getLaterality() != null ? getLaterality().hashCode() : 0);
        result = 31 * result + (isPresent() ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PhenotypeTerm that = (PhenotypeTerm) o;

        if (isPresent() != that.isPresent()) return false;
        if (!getHpoId().equals(that.getHpoId())) return false;
        if (!getName().equals(that.getName())) return false;
        if (getDefinition() != null ? !getDefinition().equals(that.getDefinition()) : that.getDefinition() != null)
            return false;
        if (getOnset() != null ? !getOnset().equals(that.getOnset()) : that.getOnset() != null) return false;
        if (getProgression() != null ? !getProgression().equals(that.getProgression()) : that.getProgression() != null)
            return false;
        if (getSeverity() != null ? !getSeverity().equals(that.getSeverity()) : that.getSeverity() != null)
            return false;
        if (getTemporalPattern() != null ? !getTemporalPattern().equals(that.getTemporalPattern()) : that.getTemporalPattern() != null)
            return false;
        if (getSpatialPattern() != null ? !getSpatialPattern().equals(that.getSpatialPattern()) : that.getSpatialPattern() != null)
            return false;
        return getLaterality() != null ? getLaterality().equals(that.getLaterality()) : that.getLaterality() == null;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PhenotypeTerm{");
        sb.append("hpoId='").append(hpoId).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", definition='").append(definition).append('\'');
        sb.append(", onset='").append(onset).append('\'');
        sb.append(", progression='").append(progression).append('\'');
        sb.append(", severity='").append(severity).append('\'');
        sb.append(", temporalPattern='").append(temporalPattern).append('\'');
        sb.append(", spatialPattern='").append(spatialPattern).append('\'');
        sb.append(", laterality='").append(laterality).append('\'');
        sb.append(", present=").append(present);
        sb.append('}');
        return sb.toString();
    }
}
