package org.monarchinitiative.application;

import javafx.concurrent.Task;
import org.monarchinitiative.io.AskServer;
import org.monarchinitiative.model.DataBucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;

import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class contains configuration of Spring beans required for text-mining analysis.
 * Created by Daniel Danis on 6/20/17.
 */
@Configuration
@Import(ScreensConfig.class)
@PropertySource("classpath:/application.properties")
public class ApplicationConfig {

    @Bean
    @Lazy
    public ExecutorService executor() {
        return Executors.newSingleThreadExecutor(
                runnable -> {
                    Thread thread = new Thread(runnable);
                    thread.setDaemon(true);
                    return thread;
                });
    }

    /**
     * Task which will query server on specified URL for JSON response with text-mining results.
     * @return {@link AskServer} instance (subclass of {@link Task}).
     * @throws Exception
     */
    @Bean
    public AskServer askServer() throws Exception {
        URL url = new URL(env.getProperty("textmining.server.url", "http://phenotyper.monarchinitiative.org:5678/cr/annotate"));
        return new AskServer(url);
    }

    @Autowired
    private Environment env;

    @Bean
    public DataBucket dataBucket() {
        return new DataBucket();
    }


}
