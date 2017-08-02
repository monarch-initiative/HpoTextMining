package org.monarchinitiative.hpotextmining.model;

import java.util.Set;
import java.util.stream.Collectors;

public class SingleTextMiningResult implements TextMiningResult {

    private final String pmid;

    private final Set<PhenotypeTerm> phenotypeTerms;

    public SingleTextMiningResult(String pmid, Set<PhenotypeTerm> phenotypeTerms) {
        this.pmid = pmid;
        this.phenotypeTerms = phenotypeTerms;
    }


    @Override
    public String getPMID() {
        return pmid;
    }

    @Override
    public Set<PhenotypeTerm> getYesTerms() {
        return phenotypeTerms.stream().filter(PhenotypeTerm::isPresent).collect(Collectors.toSet());
    }

    @Override
    public Set<PhenotypeTerm> getNotTerms() {
        return phenotypeTerms.stream().filter(t -> !t.isPresent()).collect(Collectors.toSet());
    }
}
