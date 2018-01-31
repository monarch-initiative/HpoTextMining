package com.github.monarchinitiative.hpotextmining.gui;

import com.github.monarchinitiative.hpotextmining.gui.util.UTF8Control;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.1
 * @since 0.2
 */
public final class HpoTextMiningModule extends AbstractModule {

    private final Stage window;


    public HpoTextMiningModule(final Stage window) {
        this.window = window;
    }


    @Override
    protected void configure() {

        bind(Stage.class)
                .annotatedWith(Names.named("mainWindow"))
                .toInstance(window);

        bind(ResourceBundle.class)
                .toInstance(ResourceBundle.getBundle("resource_bundle.ResourceBundle",
                        new Locale("en", "US"), new UTF8Control()));

        bind(ExecutorService.class)
                .toInstance(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));

    }
}
