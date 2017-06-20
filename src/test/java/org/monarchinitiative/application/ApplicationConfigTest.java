package org.monarchinitiative.application;

import org.monarchinitiative.TextMiningAnalysis;
import org.monarchinitiative.io.AskServer;
import org.monarchinitiative.model.DataBucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Annotation-based application config class. Create spring beans which will be used for testing of text-mining app.
 * Created by Daniel Danis on 6/6/17.
 */
@Configuration
@Import(ScreensConfigTest.class)
@PropertySource("classpath:/applicationTest.properties")
public class ApplicationConfigTest {

    @Autowired
    private Environment env;

    @Bean
    @Lazy
    public ExecutorService executor() {
        return Executors.newFixedThreadPool(env.getProperty("max.threads", Integer.class, 4),
                runnable -> {
                    Thread thread = new Thread(runnable);
                    thread.setDaemon(false);
                    return thread;
                });
    }

    @Bean
    public DataBucket dataBucket() throws IOException, URISyntaxException {
        File json = new File(getClass().getResource("/payload.json").toURI());
        String jsonPayload = readFileContent(json);

        File mined = new File(getClass().getResource("/payload.txt").toURI());
        String minedText = readFileContent(mined);

        DataBucket dataBucket = new DataBucket();
        dataBucket.setJsonResult(jsonPayload);
        dataBucket.setMinedText(minedText);
        return dataBucket;
    }

    @Bean
    public AskServer askServer() throws Exception {
        URL url = new URL(env.getProperty("textmining.server.url", "http://phenotyper.monarchinitiative.org:5678/cr/annotate"));
        return new AskServer(url);
    }

    @Bean
    public TextMiningAnalysis biolarkAnalysis() {
        return new TextMiningAnalysis();
    }


    /**
     * Convenience method for reading whole file into String.
     *
     * @param file file to be read.
     * @return string with file content
     * @throws IOException in case of I/O error.
     */
    private String readFileContent(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        int length = (int) file.length();
        byte[] data = new byte[length];
        int read = is.read(data);
        is.close();
        if (read != length) {
            throw new IOException(String.format("Bytes read (%s) doesn't match with file size (%s)", read, length));
        }
        return new String(data);
    }
}