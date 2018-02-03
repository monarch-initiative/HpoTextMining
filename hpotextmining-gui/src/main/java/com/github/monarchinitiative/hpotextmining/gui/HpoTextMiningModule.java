package com.github.monarchinitiative.hpotextmining.gui;

import com.github.monarchinitiative.hpotextmining.core.miners.HPOMiner;
import com.github.monarchinitiative.hpotextmining.core.miners.biolark.BiolarkHPOMiner;
import com.github.monarchinitiative.hpotextmining.gui.controllers.Configure;
import com.github.monarchinitiative.hpotextmining.gui.controllers.Main;
import com.github.monarchinitiative.hpotextmining.gui.controllers.OntologyTree;
import com.github.monarchinitiative.hpotextmining.gui.controllers.Present;
import com.github.monarchinitiative.hpotextmining.gui.util.UTF8Control;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import javafx.stage.Stage;
import ontologizer.io.obo.OBOParser;
import ontologizer.io.obo.OBOParserException;
import ontologizer.io.obo.OBOParserFileInput;
import ontologizer.ontology.Ontology;
import ontologizer.ontology.TermContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Singleton;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.1
 * @since 0.2
 */
public final class HpoTextMiningModule extends AbstractModule {

    public static final String APPLICATION_PROPERTIES_FILENAME = "application.properties";

    private static final Logger LOGGER = LogManager.getLogger();

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

        // ---- CONTROLLERS ----
        bind(Main.class).in(Singleton.class);

        bind(OntologyTree.class).in(Singleton.class);

        bind(Configure.class).in(Singleton.class);

        bind(Present.class).in(Singleton.class);
        // ---- CONTROLLERS ----
    }


    @Provides
    @Singleton
    private OptionalService optionalServices(Properties properties) {
        OptionalService service = new OptionalService();

        File ontologyFile = new File(properties.getProperty("hp.obo.path"));
        if (ontologyFile.isFile()) {
            try {
                OBOParser parser = new OBOParser(new OBOParserFileInput(ontologyFile.getPath()),
                        OBOParser.PARSE_DEFINITIONS);
                LOGGER.info(parser.doParse());
                TermContainer termContainer = new TermContainer(parser.getTermMap(), parser.getFormatVersion(), parser
                        .getDate());
                service.setOntology(Ontology.create(termContainer));
            } catch (IOException | OBOParserException e) {
                LOGGER.warn(e);
                service.setOntology(null);
            }
        } else {
            LOGGER.warn("Invalid path to HP obo file: {}", ontologyFile.getPath());
            service.setOntology(null);
        }

        return service;
    }


    @Provides
    private HPOMiner hpoMiner(Properties properties) throws MalformedURLException {
        return new BiolarkHPOMiner(new URL(properties.getProperty("biolark.url")));
    }


    @Provides
    private Properties properties() throws IOException {
        Properties properties = new Properties();
        getApplicationPropertiesPath().ifPresent(file -> {
            try {
                properties.load(new FileReader(file));
            } catch (IOException e) {
                LOGGER.warn(e);
            }
        });
        return properties;
    }


    /**
     * Get path to directory where the code (classes or JAR file) is located.
     *
     * @return {@link Optional} of {@link File} path of the directory with code. Empty, if the <code>UTF-8</code> is
     * not supported or if the classes are not being loaded from file
     */
    static Optional<File> getCodeHomePath() {
        try {
            File file = new File(URLDecoder.decode(ClassLoader.getSystemClassLoader().getResource(".")
                    .getPath(), "UTF-8"));
            return Optional.of(file);
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn("Unable to resolve path of the running JAR file");
            return Optional.empty();
        }
    }


    /**
     * Get path to 'application.properties' file, where the App properties are expected to be defined.
     *
     * @return {@link Optional} of {@link File}
     */
    static Optional<File> getApplicationPropertiesPath() {
        if (getCodeHomePath().isPresent())
            return Optional.of(new File(getCodeHomePath().get(), APPLICATION_PROPERTIES_FILENAME));
        else
            return Optional.empty();
    }

}
