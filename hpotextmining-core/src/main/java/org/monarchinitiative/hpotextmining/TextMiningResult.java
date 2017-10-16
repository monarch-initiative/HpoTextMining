package org.monarchinitiative.hpotextmining;

import org.monarchinitiative.hpotextmining.model.PhenotypeTerm;

import java.util.Set;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version X.Y.Z
 * @since X.Y
 */
public interface TextMiningResult {

    Set<PhenotypeTerm> getTerms();

    String getPmid();
}
