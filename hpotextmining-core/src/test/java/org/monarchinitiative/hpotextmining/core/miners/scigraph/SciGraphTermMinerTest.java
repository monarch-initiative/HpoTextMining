package org.monarchinitiative.hpotextmining.core.miners.scigraph;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.hpotextmining.core.miners.MinedTerm;
import org.monarchinitiative.hpotextmining.core.miners.SimpleMinedTerm;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * This class tests connection to Monarch text annotation services connection. Use it to send query text to the connection and
 * receive corresponding JSON response.
 *
 * @author <a href="mailto:aaron.zhang@jax.org">Aaron Zhang</a>
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.2
 * @since 0.2.2
 */
public class SciGraphTermMinerTest {

    private static URL realScigraphUrl;

    private static String payload;

    private static String scigraphJsonResponse;

    public URLConnection connection;

    public SciGraphTermMiner.ConnectionFactory factory;

    @BeforeAll
    public static void beforeClassSetUp() throws Exception {
        String path = "/scigraph/annotations/complete";
        String base = "https://scigraph-ontology.monarchinitiative.org";
        realScigraphUrl = new URL(new URL(base), path);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(SciGraphTermMinerTest.class.getResourceAsStream("/payload.txt")))) {
            payload = reader.lines().collect(Collectors.joining("\n"));
        }

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(SciGraphTermMinerTest.class.getResource("sciGraphJsonResponse.json").toURI()))) {
            scigraphJsonResponse = reader.lines().collect(Collectors.joining("\n"));
        }
    }

    @BeforeEach
    public void setUp() {
        connection = Mockito.mock(URLConnection.class);
        factory = Mockito.mock(SciGraphTermMiner.ConnectionFactory.class);
    }

    @Test
    public void mineHpoTermsFromLargePayload() throws Exception {
        URL mockUrl = new URL("file:/some/path/to/file");
        Mockito.when(connection.getURL()).thenReturn(mockUrl);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Mockito.when(connection.getOutputStream()).thenReturn(os);

        ByteArrayInputStream is = new ByteArrayInputStream(scigraphJsonResponse.getBytes());
        Mockito.when(connection.getInputStream()).thenReturn(is);

        Mockito.when(factory.getConnection()).thenReturn(connection);
        SciGraphTermMiner instance = new SciGraphTermMiner(factory);
        final Set<MinedTerm> minedTerms = instance.doMining(payload);

        // test results - we should have 66 MinedTerms in total. Then, we test presence of five terms picked at random
        assertThat(minedTerms.size(), is(66));
        assertThat(minedTerms, hasItem(new SimpleMinedTerm(1635, 1643, "HP:0001762", true)));
        assertThat(minedTerms, hasItem(new SimpleMinedTerm(2804, 2822, "MP:0000752", true)));
        assertThat(minedTerms, hasItem(new SimpleMinedTerm(2353, 2363, "HP:0200055", true)));
        assertThat(minedTerms, hasItem(new SimpleMinedTerm(414, 436, "HP:0001290", true)));
        assertThat(minedTerms, hasItem(new SimpleMinedTerm(1436, 1445, "HP:0002650", true)));


        // test that we query the server in a consistent way
        assertThat(os.toString().length(), is(4055));
        assertThat(os.toString().hashCode(), is(-478580216));
    }


    /**
     * Read query text, send it to the connection and print received response to STDOUT.
     *
     * @throws Exception bla
     */
    @Test
    @Disabled // Ignored because requires connection to the real server. We mock this connection in the other tests
    public void askMonarchServerTest() throws Exception {
        SciGraphTermMiner miner = new SciGraphTermMiner(realScigraphUrl);
//        Mockito.when(connection.openStream()).thenReturn(new ByteArrayInputStream(payload.getBytes()));


        Set<MinedTerm> minedTerms = miner.doMining(payload);
        minedTerms.forEach(System.err::println);
    }

}