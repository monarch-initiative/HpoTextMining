package org.monarchinitiative.hpotextmining.core.miners.scigraph;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.monarchinitiative.hpotextmining.core.miners.biolark.BiolarkResult;
import org.monarchinitiative.hpotextmining.core.miners.biolark.BiolarkToken;

import java.util.HashSet;
import java.util.Set;

/**
 * JSON Class representation of Monarch SciGraph text mining results.
 *
 * @author <a href="mailto:aaron.zhang@jax.org">Aaron Zhang</a>
 * @version 0.1.0
 * @since 0.2.2
 */
public class SciGraphResult implements Comparable<SciGraphResult> {

    private SciGraphToken token;

    private int start;

    private int end;

    @JsonCreator
    public SciGraphResult(@JsonProperty("token") SciGraphToken token,
                          @JsonProperty("start") int start,
                          @JsonProperty("end") int end) {
        this.token = token;
        this.start = start;
        this.end = end;

    }

    /**
     * Convert to BiolarkResult.
     *
     * @param m     {@link SciGraphResult}
     * @param query {@link String} with the query text
     * @return {@link BiolarkResult} representing the <code>m</code> instance
     */
    public static BiolarkResult toBiolarkResult(SciGraphResult m, String query) {

        String id = m.token.getId();
        String label = m.token.getTerms().get(0);
        Set<String> synonyms = new HashSet<>();
        BiolarkToken biolarkTerm = new BiolarkToken(id, label, synonyms);

        return new BiolarkResult(m.start,
                m.end,
                m.end - m.start,
                query.substring(m.start, m.end),
                m.token.getId().split(":")[0],
                biolarkTerm,
                false);
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
        if (!(other instanceof SciGraphResult)) {
            return false;
        }

        SciGraphResult o = (SciGraphResult) other;
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
     *
     * @param o other {@link SciGraphResult}
     * @return result as specified by {@link Comparable}
     */
    @Override
    public int compareTo(SciGraphResult o) {
        return this.start - o.start;
    }

}
