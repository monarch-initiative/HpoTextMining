package com.github.monarchinitiative.hpotextmining.core.miners;

import com.google.common.collect.ComparisonChain;

import java.util.Comparator;

/**
 * This class is a POJO for representation of a <code>termId</code> identified by text-mining.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.1
 * @see TermMiner
 * @since 0.2
 */
public final class SimpleMinedTerm implements MinedTerm {

    private final int begin;

    private final int end;


    private final String termId;

    /**
     * Boolean flag indicating that the termId was either present or absent in the patient/proband who is
     * being described by the mined text
     */
    private final boolean present;


    public SimpleMinedTerm(int begin, int end, String termId, boolean present) {
        this.begin = begin;
        this.end = end;
        this.termId = termId;
        this.present = present;
    }

    public static Comparator<MinedTerm> compareByBegin() {
        return (l, r) -> ComparisonChain.start().compare(l.getBegin(), r.getBegin()).result();
    }

    @Override
    public int getBegin() {
        return begin;
    }

    @Override
    public int getEnd() {
        return end;
    }

    @Override
    public String getTermId() {
        return termId;
    }

    @Override
    public boolean isPresent() {
        return present;
    }

    @Override
    public int hashCode() {
        int result = begin;
        result = 31 * result + end;
        result = 31 * result + termId.hashCode();
        result = 31 * result + (present ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleMinedTerm minedTerm = (SimpleMinedTerm) o;

        if (begin != minedTerm.begin) return false;
        if (end != minedTerm.end) return false;
        if (present != minedTerm.present) return false;
        return termId.equals(minedTerm.termId);
    }

    @Override
    public String toString() {
        return "SimpleMinedTerm{" +
                "begin=" + begin +
                ", end=" + end +
                ", termId='" + termId + '\'' +
                ", present=" + present +
                '}';
    }
}
