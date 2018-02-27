package com.github.monarchinitiative.hpotextmining.core.miners.biolark;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.github.monarchinitiative.hpotextmining.core.miners.HPOMiner;
import com.github.monarchinitiative.hpotextmining.core.miners.MinedTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This {@link HPOMiner} uses <em>Biolark</em> web API to discover positions of HPO terms within provided query.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.1
 * @since 0.2
 */
public final class BiolarkHPOMiner implements HPOMiner {

    private static final Logger LOGGER = LoggerFactory.getLogger(BiolarkHPOMiner.class);

    private final URL biolarkUrl;


    public BiolarkHPOMiner(final URL biolarkUrl) {
        this.biolarkUrl = biolarkUrl;
    }


    /**
     * Performs mining of the provided text using Biolark service; <code>query</code> String is sent to the Biolark
     * API and the Thread is blocked until a <em>JSON</em> response is received. The response is then decoded into
     * {@link MinedTerm}s.
     *
     * @param query String with text to be mined for HPO terms
     * @return {@link Set} of {@link MinedTerm}s representing identified HPO terms. The set is empty, if I/O error
     * occurs or if URL/query are invalid
     */
    @Override
    public Set<MinedTerm> doMining(final String query) throws Exception {
        if (query == null) {
            String msg = "You must set query to be able to start analysis";
            LOGGER.warn(msg);
            throw new IllegalStateException(msg);
        } else if (biolarkUrl == null) {
            String msg = "You must set Biolark server URL to be able to start analysis";
            LOGGER.warn(msg);
            throw new IllegalStateException(msg);
        }

        final StringBuilder jsonStringBuilder = new StringBuilder();
        final HttpURLConnection connection = (HttpURLConnection)
                biolarkUrl.openConnection();

        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Content-Type",
                "application/json; charset=UTF-8");
        final OutputStreamWriter writer = new
                OutputStreamWriter(connection.getOutputStream(), "UTF-8");
        writer.write(query);
        writer.close();
        final BufferedReader br = new BufferedReader(new
                InputStreamReader(connection.getInputStream()));

        String line;
        while ((line = br.readLine()) != null) {
            jsonStringBuilder.append(line);
        }
        br.close();
        connection.disconnect();


        final Set<BiolarkResult> set = decodePayload(jsonStringBuilder.toString());
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
            LOGGER.warn(e.getMessage(), e);
            return new HashSet<>();
        }
    }

}
