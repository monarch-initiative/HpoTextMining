package com.github.monarchinitiative.hpotextmining.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ComparisonChain;

import java.util.Comparator;

/**
 * The server performing text-mining analysis returns results as a collection of JSON objects. This class serves as a
 * model for those objects. Created by Daniel Danis on 6/19/17.
 */
public class BiolarkResult {

    private final int start;

    private final int end;

    private final int length;

    private final String original_text;

    private final String source;

    private final SimpleBiolarkTerm term;

    private final boolean negated;

    /**
     * Create instance from single response object. This instance contains coordinates of the "term-containing"
     * substring within the analyzed text.
     *
     * @param start         start offset of substring in the original (text-mined) text.
     * @param end           end offset of substring in the original (text-mined) text.
     * @param length        length of the substring.
     * @param original_text substring text.
     * @param source        not sure what this is but it is always "HPO"
     * @param term          identified {@link SimpleBiolarkTerm}
     * @param negated       indicates absence/non-presence of this label in the patient
     */
    @JsonCreator
    public BiolarkResult(@JsonProperty("start_offset") int start,
                         @JsonProperty("end_offset") int end,
                         @JsonProperty("length") int length,
                         @JsonProperty("original_text") String original_text,
                         @JsonProperty("source") String source,
                         @JsonProperty("term") SimpleBiolarkTerm term,
                         @JsonProperty("negated") boolean negated) {
        this.start = start;
        this.end = end;
        this.length = length;
        this.original_text = original_text;
        this.source = source;
        this.term = term;
        this.negated = negated;
    }

    public static Comparator<BiolarkResult> compareByStart() {
        return (left, right) -> ComparisonChain.start()
                .compare(left.getStart(), right.getStart())
                .result();
    }

    public static Comparator<BiolarkResult> compareByName() {
        return (left, right) -> ComparisonChain.start()
                .compare(left.getTerm().getLabel(), right.getTerm().getLabel())
                .result();
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getLength() {
        return length;
    }

    public String getOriginal_text() {
        return original_text;
    }

    public String getSource() {
        return source;
    }

    public SimpleBiolarkTerm getTerm() {
        return term;
    }

    public boolean isNegated() {
        return negated;
    }

    @Override
    public int hashCode() {
        int result = start;
        result = 31 * result + end;
        result = 31 * result + length;
        result = 31 * result + original_text.hashCode();
        result = 31 * result + source.hashCode();
        result = 31 * result + term.hashCode();
        result = 31 * result + (negated ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BiolarkResult that = (BiolarkResult) o;

        if (start != that.start) return false;
        if (end != that.end) return false;
        if (length != that.length) return false;
        if (negated != that.negated) return false;
        if (!original_text.equals(that.original_text)) return false;
        if (!source.equals(that.source)) return false;
        return term.equals(that.term);
    }

    @Override
    public String toString() {
        return "BiolarkResult{" +
                "start=" + start +
                ", end=" + end +
                ", length=" + length +
                ", original_text='" + original_text + '\'' +
                ", source='" + source + '\'' +
                ", term=" + term +
                ", negated=" + negated +
                '}';
    }
}
