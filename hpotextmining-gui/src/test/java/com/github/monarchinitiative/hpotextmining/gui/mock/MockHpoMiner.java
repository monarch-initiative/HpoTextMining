package com.github.monarchinitiative.hpotextmining.gui.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.github.monarchinitiative.hpotextmining.core.miners.HPOMiner;
import com.github.monarchinitiative.hpotextmining.core.miners.MinedTerm;
import com.github.monarchinitiative.hpotextmining.core.miners.biolark.BiolarkResult;
import com.github.monarchinitiative.hpotextmining.gui.controllers.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.1
 * @since 0.2
 */
public class MockHpoMiner implements HPOMiner {


    @Override
    public Set<MinedTerm> doMining(String query) throws Exception {
        String mockResponse = mockResponseReader();
        final Set<BiolarkResult> set = decodePayload(mockResponse);

        return set.stream()
                .map(res -> new MinedTerm(res.getStart(), res.getEnd(), res.getTerm().getId(), !res.isNegated()))
                .collect(Collectors.toSet());
    }


    /**
     * Parse JSON string into set of intermediate result objects.
     *
     * @param jsonResponse JSON string to be parsed
     * @return possibly empty set of {@link BiolarkResult} objects
     */
    private Set<BiolarkResult> decodePayload(String jsonResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            CollectionType javaType = mapper.getTypeFactory().constructCollectionType(Set.class, BiolarkResult.class);
            return mapper.readValue(jsonResponse, javaType);
        } catch (IOException e) {
            return new HashSet<>();
        }
    }


    /**
     * @return Stirng with mock JSON response
     * @throws IOException bla
     */
    private String mockResponseReader() throws IOException {
        try (BufferedReader queryTextReader = new BufferedReader(
                new InputStreamReader(MockHpoMiner.class.getResourceAsStream("jsonResponse.txt")))) {
            return queryTextReader.lines().collect(Collectors.joining("\n"));
        }

    }
}
