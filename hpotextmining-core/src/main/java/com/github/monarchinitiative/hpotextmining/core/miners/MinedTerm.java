package com.github.monarchinitiative.hpotextmining.core.miners;

import com.google.common.collect.ComparisonChain;

import java.util.Comparator;

/**
 * This class is a POJO for representation of a HPO term identified by text-mining.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.1
 * @see HPOMiner
 * @since 0.2
 */
public final class MinedTerm {

    /**
     * Zero-based begin coordinate (like in BED format) of text region, based on which this HPO term has been
     * identified. The coordinate is with reference to the whole text that has been mined for HPO terms
     */
    private final int begin;

    /**
     * One-based end coordinate (like in BED format) of text region, based on which this HPO term has been
     * identified. The coordinate is with reference to the whole text that has been mined for HPO terms
     */
    private final int end;

    /**
     * ID of the HPO term that is being represented by this MinedTerm. E.g. HP:0003198
     */
    private final String termId;

    /**
     * Boolean flag indicating that the term was either present or absent in the patient/proband who is
     * being described by the mined text
     */
    private final boolean present;


    public MinedTerm(int begin, int end, String termId, boolean present) {
        this.begin = begin;
        this.end = end;
        this.termId = termId;
        this.present = present;
    }


    public int getBegin() {
        return begin;
    }


    public int getEnd() {
        return end;
    }


    public String getTermId() {
        return termId;
    }


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

        MinedTerm minedTerm = (MinedTerm) o;

        if (begin != minedTerm.begin) return false;
        if (end != minedTerm.end) return false;
        if (present != minedTerm.present) return false;
        return termId.equals(minedTerm.termId);
    }


    @Override
    public String toString() {
        return "MinedTerm{" +
                "begin=" + begin +
                ", end=" + end +
                ", termId='" + termId + '\'' +
                ", present=" + present +
                '}';
    }


    public static Comparator<MinedTerm> compareByBegin() {
        return (l, r) -> ComparisonChain.start().compare(l.getBegin(), r.getBegin()).result();
    }
}
