package org.monarchinitiative.hpotextmining.core.miners.scigraph;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.monarchinitiative.hpotextmining.core.miners.MinedTerm;
import org.monarchinitiative.hpotextmining.core.miners.SimpleMinedTerm;
import org.monarchinitiative.hpotextmining.core.miners.TermMiner;
import org.monarchinitiative.hpotextmining.core.miners.TermMinerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * This class allows using Monarch SciGraph Server for text mining.
 * TODO: take out the server configuration parameters to generalize it (and merge it to AskTudorServer) if necessary
 *
 * @author <a href="mailto:aaron.zhang@jax.org">Aaron Zhang</a>
 * @version 0.2.3
 * @since 0.2.2
 */
public class SciGraphTermMiner implements TermMiner {

    private static final Logger LOGGER = LoggerFactory.getLogger(SciGraphTermMiner.class);

    private final ObjectMapper objectMapper;

    private final ConnectionFactory connectionFactory;

    public SciGraphTermMiner(URL server) {
        this(new ConnectionFactory(server));
    }

    /**
     * @param factory {@link ConnectionFactory} for making {@link URLConnection}s
     */
    SciGraphTermMiner(ConnectionFactory factory) {
        this.connectionFactory = factory; // the factory makes possible to mock & test. We need to do that because class `URL` is final
        this.objectMapper = new ObjectMapper();
    }

    private static String getParamsString(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }


    /**
     * @return {@link Function} mapping {@link SciGraphResult} to {@link MinedTerm}
     */
    private static Function<SciGraphResult, MinedTerm> toMinedTerm() {
        return sgr -> new SimpleMinedTerm(sgr.getStart(), sgr.getEnd(), sgr.getToken().getId(), true);
    }


    /**
     * Performs mining of the provided text using SciGraph service; <code>query</code> String is sent to the SciGraph
     * API and the Thread is blocked until a <em>JSON</em> response is received. The response is then decoded into
     * {@link MinedTerm}s.
     *
     * @param query String with text to be mined for HPO terms
     * @return {@link Set} of {@link MinedTerm}s representing identified HPO terms. The set is empty, if I/O error
     * occurs or if URL/query are invalid
     */
    @Override
    public Set<MinedTerm> doMining(String query) throws TermMinerException {
        if (query == null) {
            throw new TermMinerException("Query must not be null");
        }

        StringBuilder jsonStringBuilder = new StringBuilder();
        try {
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("content", query);
            parameters.put("includeCat", "phenotype"); //only retrieve phenotypes
            parameters.put("includeAcronym", "true");


            URLConnection connection = connectionFactory.getConnection();
            String protocol = connection.getURL().getProtocol();

            try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8)) {
                writer.write(getParamsString(parameters));
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    jsonStringBuilder.append(line);
                }
            }

            if (protocol.equals("http")) {
                ((HttpURLConnection) connection).disconnect();
            } else if (protocol.equals("https")) {
                ((HttpsURLConnection) connection).disconnect();
            }

        } catch (ProtocolException e) {
            LOGGER.warn("Protocol exception", e);
            throw new TermMinerException("Protocol exception", e);
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn("Unsupported encoding", e);
            throw new TermMinerException("Unsupported encoding", e);
        } catch (UnknownHostException e) { // this happens if you are offline
            LOGGER.warn("Unable to connect to host '{}'", e.getMessage(), e);
            throw new TermMinerException("Unable to connect to host " + e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.warn("I/O error occured", e);
            throw new TermMinerException("I/O error occured", e);
        }


        //map json result into SciGraphResult objects and then to MinedTerm objects
        try {
            final SciGraphResult[] sciGraphResults = objectMapper.readValue(jsonStringBuilder.toString(), SciGraphResult[].class);

            return Arrays.stream(sciGraphResults)
                    .map(toMinedTerm())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new TermMinerException("Error occurred during parsing response from the server", e);
        }

    }

    /**
     * Each time the text mining is requested we send the query to the server using a new connection.
     */
    public static class ConnectionFactory {

        private final URL url;

        public ConnectionFactory(URL url) {
            this.url = url;
        }

        /**
         * Set up the connection.
         *
         * @return {@link URLConnection} prepared to use
         * @throws IOException in case of I/O error
         */
        URLConnection getConnection() throws IOException {
            URLConnection connection = url.openConnection();
            final String protocol = url.getProtocol();
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

            //
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(5000); //fail if no connection for 5s
            connection.setReadTimeout(30000); //fail if no read for 30s
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            return connection;
        }
    }
}
