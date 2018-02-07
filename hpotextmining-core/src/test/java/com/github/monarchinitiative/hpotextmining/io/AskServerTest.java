package com.github.monarchinitiative.hpotextmining.io;

import org.junit.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * This class doesn't really test anything at the moment (hence Ignored). Use it to send query text to the server and
 * receive corresponding JSON response.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.0
 * @since 0.2
 */
@Ignore
public class AskServerTest {

    private static URL textMiningUrl;

    private BufferedReader payloadReader;

    /**
     * "Tested" instance
     */
//    private AskServer askServer;


    /**
     * Read query text, send it to the server and print received response to STDOUT.
     *
     * @throws Exception bla
     */
    @Test
    public void just4fun() throws Exception {
//        askServer = new AskServer(textMiningUrl);
//        askServer.setQuery(payloadReader.lines().collect(Collectors.joining("\n")));
//        askServer.setOnSucceeded(e -> {
//            try {
//                System.out.println(askServer.get());
//            } catch (InterruptedException | ExecutionException e1) {
//                e1.printStackTrace();
//            }
//        });
//        System.out.println(askServer.call()); // rude calling of the task without creating new Thread
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
        textMiningUrl = new URL("http://phenotyper.monarchinitiative.org:5678/cr/annotate");
    }
}