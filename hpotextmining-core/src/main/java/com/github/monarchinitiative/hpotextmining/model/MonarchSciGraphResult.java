package com.github.monarchinitiative.hpotextmining.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ontologizer.ontology.Ontology;
import ontologizer.ontology.Prefix;
import ontologizer.ontology.Term;
import ontologizer.ontology.TermID;
import ontologizer.types.ByteString;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;


/**
 * JSON Class representation of Monarch SciGraph text mining results.
 * @author <a href="mailto:aaron.zhang@jax.org">Aaron Zhang</a>
 * @version 0.1.0
 * @since 0.2.2
 */
public class MonarchSciGraphResult implements Comparable<MonarchSciGraphResult> {

    private SciGraphToken token;
    private int start;
    private int end;

    @JsonCreator
    public MonarchSciGraphResult(@JsonProperty("token") SciGraphToken token,
                                 @JsonProperty("start") int start,
                                 @JsonProperty("end") int end) {
        this.token = token;
        this.start = start;
        this.end = end;

    }

    public SciGraphToken getToken() {
        return token;
    }

    public void setToken(SciGraphToken token) {
        this.token = token;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    @Override
    public boolean equals(Object other) {
        if (! (other instanceof MonarchSciGraphResult)) {
            return false;
        }

        MonarchSciGraphResult o = (MonarchSciGraphResult) other;
        return this.token.equals(o.token) && this.start == o.start && this.end == o.end;
    }

    @Override
    public int hashCode() {
        int hash = 31 * this.token.hashCode() + this.start;
        hash = 31 * hash + this.end;
        return hash;
    }


    /**
     * Compare by the start position
     * @param o
     * @return
     */
    @Override
    public int compareTo(MonarchSciGraphResult o) {
        return this.start - o.start;
    }

    /**
     * Convert to BiolarkResults
     * @param query
     * @return
     */
    public static BiolarkResult toBiolarkResult(MonarchSciGraphResult m, String query) {

        //@TODO: pass in HPO term to get label and synonyms
        String id = m.token.getId();
        String label = "TODO:unknown yet";
        Set<String> synonyms = new HashSet<>();
        SimpleBiolarkTerm biolarkTerm = new SimpleBiolarkTerm(m.token.getId(), label, synonyms);

        BiolarkResult biolark = new BiolarkResult(m.start,
                m.end,
                m.end - m.start,
                query.substring(m.start, m.end),
        m.token.getId().split(":")[0],
        biolarkTerm,
        false);

        return biolark;
    }

}
