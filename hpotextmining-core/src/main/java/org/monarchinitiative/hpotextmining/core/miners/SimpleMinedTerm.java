package org.monarchinitiative.hpotextmining.core.miners;

/**
 * This class is a POJO for representation of a <code>termId</code> identified by text-mining.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.1
 * @see TermMiner
 * @since 0.2
 */
record SimpleMinedTerm(int begin, int end, String termId, boolean present) implements MinedTerm {

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
