package org.monarchinitiative.hpotextmining.model;

import org.monarchinitiative.hpotextmining.TextMiningResult;

import java.util.Set;

/**
 * Container class for approved HPO terms and PMID.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.1.0
 * @since 0.1
 */
public class SimpleTextMiningResult implements TextMiningResult {

    private final Set<PhenotypeTerm> terms;

    private final String pmid;


    public SimpleTextMiningResult(Set<PhenotypeTerm> terms, String pmid) {
        this.terms = terms;
        this.pmid = pmid;
    }


    @Override
    public Set<PhenotypeTerm> getTerms() {
        return terms;
    }


    @Override
    public String getPmid() {
        return pmid;
    }


}
