package org.monarchinitiative.hpotextmining;

import org.monarchinitiative.hpotextmining.model.PhenotypeTerm;

import java.util.Set;

/**
 * Classes that implement this interface contain results of HPO analysis.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.1.0
 * @since 0.1
 */
public interface TextMiningResult {

    /**
     * Get a set of phenotype terms approved by user.
     *
     * @return {@link Set} of {@link PhenotypeTerm}s
     */
    Set<PhenotypeTerm> getTerms();

    /**
     * Get string with PMID of the publication which was used in biocuration.
     *
     * @return {@link String} with PMID number, e.g. 1234987
     */
    String getPmid();
}
