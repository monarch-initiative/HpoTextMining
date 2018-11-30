package com.github.monarchinitiative.hpotextmining.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.*;

public class SciGraphTokenTest {

    @Test
    public void simpleTest() throws Exception{
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