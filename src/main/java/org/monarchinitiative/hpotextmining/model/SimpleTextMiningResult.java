package org.monarchinitiative.hpotextmining.model;

import com.google.common.collect.ImmutableSet;
import ontologizer.ontology.Term;

import java.util.Set;

/**
 * This class is a data container for text-mining analysis results. Created by Daniel Danis on 6/20/17.
 */
public class SimpleTextMiningResult implements TextMiningResult {

    private final String pmid;

    private final ImmutableSet<PhenotypeTerm> yesTerms;

    private final ImmutableSet<PhenotypeTerm> notTerms;

    /**
     * Create instance with given values.
     *
     * @param pmid     string with PMID of publication
     * @param yesTerms set of <em>YES</em> terms approved by the curator.
     * @param notTerms set of <em>NOT</em> terms approved by the curator.
     */
    public SimpleTextMiningResult(String pmid, Set<PhenotypeTerm> yesTerms, Set<PhenotypeTerm> notTerms) {
        this.pmid = pmid;
        this.yesTerms = ImmutableSet.copyOf(yesTerms);
        this.notTerms = ImmutableSet.copyOf(notTerms);
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getPMID() {
        return pmid;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Set<PhenotypeTerm> getYesTerms() {
        return yesTerms;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Set<PhenotypeTerm> getNotTerms() {
        return notTerms;
    }

    @Override
    public String toString() {
        return "SimpleTextMiningResult{" +
                "pmid='" + pmid + '\'' +
                ", yesTerms=" + yesTerms +
                ", notTerms=" + notTerms +
                '}';
    }
}
