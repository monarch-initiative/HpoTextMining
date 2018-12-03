package com.github.monarchinitiative.hpotextmining.core.miners.scigraph;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.monarchinitiative.hpotextmining.core.miners.MinedTerm;
import com.github.monarchinitiative.hpotextmining.core.miners.SimpleMinedTerm;
import com.github.monarchinitiative.hpotextmining.core.miners.TermMiner;
import com.github.monarchinitiative.hpotextmining.core.miners.TermMinerException;
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
 */
public class SciGraphTermMiner implements TermMiner {

    private static final Logger LOGGER = LoggerFactory.getLogger(SciGraphTermMiner.class);

    private final ObjectMapper objectMapper;

    private final URLConnection connection;

    public SciGraphTermMiner(URL server) throws IOException {
        this(server.openConnection());
    }

    public SciGraphTermMiner(URLConnection connection) {
        this.connection = connection;
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

    @Override
    public Set<MinedTerm> doMining(String query) throws TermMinerException {
        if (query == null) {
            throw new TermMinerException("Query must not be null");
        }

        StringBuilder jsonStringBuilder = new StringBuilder();
        try {
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("content", query);
            parameters.put("includeCat", "Phenotype"); //only retrieve phenotypes
            parameters.put("includeAcronym", "true");

            final String protocol = connection.getURL().getProtocol();
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
            connection.setConnectTimeout(5000); //fail if no connection for 5s
            connection.setReadTimeout(30000); //fail if no read for 30s
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

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


}
