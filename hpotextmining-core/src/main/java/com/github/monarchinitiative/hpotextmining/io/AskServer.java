package com.github.monarchinitiative.hpotextmining.io;

import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Task to be used to query server URL to asynchronously retrieve results of text-mining in JSON format.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.1.0
 * @since 0.1
 */
public class AskServer extends Task<String> {

    private final URL server;

    private String query = null;

    public AskServer(URL server) {
        this.server = server;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    protected String call() throws Exception {
        if (query == null) {
            throw new IllegalStateException("You must set query to be able to start analysis");
        }
        StringBuilder jsonStringBuilder = new StringBuilder();

        try {
            HttpURLConnection connection = (HttpURLConnection)
                    server.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type",
                    "application/json; charset=UTF-8");
            OutputStreamWriter writer = new
                    OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(query);
            writer.close();
            BufferedReader br = new BufferedReader(new
                    InputStreamReader(connection.getInputStream()));

            String line;
            while ((line = br.readLine()) != null) {
                jsonStringBuilder.append(line);
            }
            br.close();
            connection.disconnect();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return jsonStringBuilder.toString();
    }
}
