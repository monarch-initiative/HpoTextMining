package org.monarchinitiative.hpotextmining.model;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * This class is a data container for text-mining analysis results. Created by Daniel Danis on 6/20/17.
 */
public class SimpleTextMiningResult implements TextMiningResult {

    private final String pmid;

    private final ImmutableSet<PhenotypeTerm> phenotypeTerms;

    /**
     * Create instance with given values.
     *
     * @param pmid           string with PMID of publication
     * @param phenotypeTerms set of {@link PhenotypeTerm} objects representing terms approved by the curator.
     */
    public SimpleTextMiningResult(String pmid, Set<PhenotypeTerm> phenotypeTerms) {
        this.pmid = pmid;
        this.phenotypeTerms = ImmutableSet.copyOf(phenotypeTerms);
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getPMID() {
        return pmid;
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public Set<PhenotypeTerm> getPhenotypeTerms() {
        return phenotypeTerms;
    }

    @Override
    public String toString() {
        return "SimpleTextMiningResult{" +
                "pmid='" + pmid + '\'' +
                ", phenotypeTerms=" + phenotypeTerms +
                '}';
    }
}
