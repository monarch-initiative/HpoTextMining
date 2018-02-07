package com.github.monarchinitiative.hpotextmining.core.miners;

import java.util.Set;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.1
 * @since 0.2
 */
public interface HPOMiner {

    Set<MinedTerm> doMining(final String query) throws Exception;

}
