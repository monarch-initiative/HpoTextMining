package org.monarchinitiative.hpotextmining.core.miners.biolark;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.monarchinitiative.hpotextmining.core.miners.MinedTerm;
import org.monarchinitiative.hpotextmining.core.miners.TermMiner;
import org.monarchinitiative.hpotextmining.core.miners.TermMinerException;
import org.monarchinitiative.hpotextmining.core.miners.scigraph.SciGraphTermMiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

/**
 * This {@link TermMiner} uses <em>Biolark</em> web API to discover positions of HPO terms within provided query.
 * <p>
 * <b>Warning</b>This Miner might not be working properly at the moment since the Biolark API is not running. Use
 * {@link SciGraphTermMiner} instead
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.1
 * @since 0.2
 */
public class BiolarkTermMiner implements TermMiner {

    private static final Logger LOGGER = LoggerFactory.getLogger(BiolarkTermMiner.class);

    private final URLConnection connection;

    public BiolarkTermMiner(URLConnection connection) {
        this.connection = connection;
    }

    public BiolarkTermMiner(URL server) throws IOException {
        this(server.openConnection());
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
    public Set<MinedTerm> doMining(final String query) throws TermMinerException {
        if (query == null) {
            throw new TermMinerException("Query must not be null");
        }
        final StringBuilder jsonStringBuilder = new StringBuilder();

        try {
            String protocol = connection.getURL().getProtocol();

            switch (protocol) {
                case "http":
                    ((HttpURLConnection) connection).setRequestMethod("POST");
                    break;
                case "https":
                    ((HttpsURLConnection) connection).setRequestMethod("POST");
                    break;
                default:
                    break;
            }

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            try (OutputStreamWriter writer = new
                    OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8)) {
                writer.write(query);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    jsonStringBuilder.append(line);
                }
            }

//            connection.disconnect();

        } catch (IOException e) {
            throw new TermMinerException("Error occurred during the connection", e);
        }

        return decodePayload(jsonStringBuilder.toString());

    }


    /**
     * Parse JSON string into set of intermediate result objects.
     *
     * @param jsonResponse JSON string to be parsed
     * @return possibly empty set of {@link MinedTerm} objects
     */
    private Set<MinedTerm> decodePayload(String jsonResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            CollectionType javaType = mapper.getTypeFactory().constructCollectionType(Set.class, BiolarkResult.class);
            Set<BiolarkResult> results = mapper.readValue(jsonResponse, javaType);
            return new HashSet<>();
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
            return new HashSet<>();
        }
    }

}
