package org.monarchinitiative.hpotextmining.core.miners.scigraph;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author <a href="mailto:aaron.zhang@jax.org">Aaron Zhang</a>
 * @version 0.2.2
 * @since 0.2.2
 */
public class SciGraphTokenTest {

    @Test
    public void simpleTest() throws Exception {
        final String token = "{\"id\":\"MP:0000751\",\"categories\":[\"Phenotype\"],\"terms\":[\"myopathy\"]}";
        ObjectMapper mapper = new ObjectMapper();
        SciGraphToken obj = mapper.readValue(token, SciGraphToken.class);
        assertNotNull(obj);
        assertEquals(obj.getId(), "MP:0000751");
        assertEquals(obj.getCategories().get(0), "Phenotype");
        assertEquals(obj.getTerms().get(0), "myopathy");
        assertEquals(obj.getTerms().size(), 1);
    }

}