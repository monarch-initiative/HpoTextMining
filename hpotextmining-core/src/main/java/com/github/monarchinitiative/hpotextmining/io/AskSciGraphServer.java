package com.github.monarchinitiative.hpotextmining.io;

import javafx.concurrent.Task;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class AskSciGraphServer extends AskServer {
    private final URL server;

    private String query = null;

    public AskSciGraphServer(URL server) {
        this.server = server;
    }

    @Override
    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    protected String call() throws Exception {
        if (query == null) {
            throw new IllegalStateException("You must set query to be able to start analysis");
        }
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("content", query);
        parameters.put("includeCat", "Phenotype"); //only retrieve phenotypes
        parameters.put("includeAcronym", "true");
        StringBuilder jsonStringBuilder = new StringBuilder();

        try {
            HttpURLConnection connection = (HttpURLConnection)
                    server.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(5000); //fail if no connection for 5s
            connection.setReadTimeout(30000); //fail if no read for 30s
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded; charset=UTF-8");
            OutputStreamWriter writer = new
                    OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(getParamsString(parameters));
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

    static String getParamsString(Map<String, String> params)
            throws UnsupportedEncodingException {
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
}
