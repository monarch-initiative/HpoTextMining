package org.monarchinitiative.hpotextmining.application;

import com.genestalker.springscreen.core.FXMLDialog;
import com.sun.javafx.application.PlatformImpl;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.testfx.api.FxToolkit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Configuration of {@link FXMLDialog}s used in text-mining application. Created by Daniel Danis on 6/19/17.
 */
@Configuration
public class HPOAnalysisScreenConfig implements InitializingBean {

    @Bean
    public HPOAnalysisController hpoAnalysisController() {
        return new HPOAnalysisController(this);
    }

    @Bean
    public Parent configureDialog() {
        return FXMLDialog.loadParent(configureController(), getClass().getResource("/fxml/ConfigureView.fxml"));
    }

    @Bean
    public ConfigureController configureController() {
        return new ConfigureController(this);
    }

    @Bean
    public PresentController presentController() {
        return new PresentController(this);
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(1, runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        });
    }

    /**
     * {@inheritDoc}
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        initFX();
    }

    /**
     * Initialize JavaFX toolkit.
     */
    private synchronized void initFX() {
        PlatformImpl.startup(() -> {});
    }
}
