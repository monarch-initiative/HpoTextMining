package com.github.monarchinitiative.hpotextmining.core.miners;

import java.util.Collection;
import java.util.Set;

/**
 * Classes implementing this interface are able to mine a set of {@link MinedTerm}s from given <code>query</code>.
 * <p>
 * The query is a free text, usually containing a description of a clinical situation of the patient/proband. The aim of
 * the text-mining is to identify a set of HPO terms that represent patient's phenotype. Terms may be either present
 * or absent (see {@link MinedTerm} for more info). Set of identified terms is presented to the user/curator for
 * further approval/rejection.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.1
 * @since 0.2
 */
public interface TermMiner {

    /**
     * Parse given <code>query</code> String and return set of {@link SimpleMinedTerm}s representing HPO terms identified
     * in the <code>query</code>.
     * <p>
     * The <em>mining</em> process might be blocking so it would be nice to perform the mining on another thread than the
     * event loop thread of a Gui.
     *
     * @param query {@link String} containing text about to be searched for HPO terms
     * @return {@link Set} of {@link SimpleMinedTerm}s representing HPO terms and their positions in the
     * <code>query</code> text
     * @throws TermMinerException if there is any problem with HPO mining
     */
    Collection<MinedTerm> doMining(final String query) throws TermMinerException;
}
