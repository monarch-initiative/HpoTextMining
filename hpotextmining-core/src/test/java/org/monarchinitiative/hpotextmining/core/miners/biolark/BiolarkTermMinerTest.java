package org.monarchinitiative.hpotextmining.core.miners.biolark;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.hpotextmining.core.miners.MinedTerm;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class doesn't really test anything at the moment (hence Ignored). Use it to send query text to the server and
 * receive corresponding {@link MinedTerm}s.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.0
 * @since 0.2
 */
public class BiolarkTermMinerTest {

    private static String payload;

    private static String biolarkJsonResponse;

    private static URL textMiningUrl;

    public URLConnection connection;

    @BeforeAll
    public static void beforeClassSetUp() throws Exception {
        textMiningUrl = new URL("http://phenotyper.monarchinitiative.org:5678/cr/annotate");
//        textMiningUrl = new URL(new URL(base), path);
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(BiolarkTermMinerTest.class.getResource("/payload.txt").toURI()))) {
            payload = reader.lines().collect(Collectors.joining("\n"));
        }

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(BiolarkTermMinerTest.class.getResource("biolarkJsonResponse.json").toURI()))) {
            biolarkJsonResponse = reader.lines().collect(Collectors.joining("\n"));
        }
    }

    @BeforeEach
    public void setUp() {
        connection = Mockito.mock(URLConnection.class);
    }

    @Test
    @Disabled // ignored until Biolark functionality will be reimplemented.
    public void mineHpoTermsFromLargePayload() throws Exception {
        URL mockUrl = new URL("file://some/path/to/file");
        Mockito.when(connection.getURL()).thenReturn(mockUrl);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Mockito.when(connection.getOutputStream()).thenReturn(os);

        ByteArrayInputStream is = new ByteArrayInputStream(biolarkJsonResponse.getBytes());
        Mockito.when(connection.getInputStream()).thenReturn(is);

        BiolarkTermMiner instance = new BiolarkTermMiner(connection);
        final Set<MinedTerm> minedTerms = instance.doMining(payload);
        System.out.println("The terms");
        minedTerms.forEach(System.out::println);

        System.out.println("Output stream content:");
        System.out.println(os.toString());
    }

    /**
     * Read query text, send it to the server and print received response to STDOUT.
     *
     * @throws Exception bla
     */
    @Test
    @Disabled
    public void getResponseFromBiolark() throws Exception {
        BiolarkTermMiner instance = new BiolarkTermMiner(textMiningUrl);
        final Set<MinedTerm> minedTerms = instance.doMining(payload);
        String allTerms = minedTerms.stream().map(Objects::toString).collect(Collectors.joining("\n", "Identified terms", ""));
        System.out.println(allTerms);
    }
}