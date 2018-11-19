package com.github.monarchinitiative.hpotextmining.io;

import org.junit.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * This class test connection to Monarch text annotation services server. Use it to send query text to the server and
 * receive corresponding JSON response.
 *
 * @author <a href="mailto:aaron.zhang@jax.org">Aaron Zhang</a>
 * @version 0.2.2
 * @since 0.2.2
 */
public class AskSciGraphServerTest {

    private static String base = "https://scigraph-ontology.monarchinitiative.org";
    private static String path = "/scigraph/annotations/complete";
    private static URL textMiningUrl;

    private BufferedReader payloadReader;

    /**
     * "Tested" instance
     */
    private AskSciGraphServer askServer;


    /**
     * Read query text, send it to the server and print received response to STDOUT.
     *
     * @throws Exception bla
     */
    @Test
    public void askMonarchServerTest() throws Exception {
        askServer = new AskSciGraphServer(textMiningUrl);
        askServer.setQuery(payloadReader.lines().collect(Collectors.joining("\n")));
        askServer.setOnSucceeded(e -> {
            try {
                System.out.println(askServer.get());
            } catch (InterruptedException | ExecutionException e1) {
                e1.printStackTrace();
            }
        });
        System.out.println(askServer.call()); // rude calling of the task without creating new Thread
    }


    @Before
    public void setUp() throws Exception {
        payloadReader = new BufferedReader(
                new InputStreamReader(AskServerTest.class.getResourceAsStream("/payload.txt")));
    }


    @After
    public void tearDown() throws Exception {
        payloadReader.close();
    }


    @BeforeClass
    public static void beforeClassSetUp() throws Exception {
        textMiningUrl = new URL(new URL(base), path);
    }
}