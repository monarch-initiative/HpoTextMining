package com.github.monarchinitiative.hpotextmining.gui;

import com.github.monarchinitiative.hpotextmining.core.miners.HPOMiner;
import com.github.monarchinitiative.hpotextmining.core.miners.biolark.BiolarkHPOMiner;
import com.github.monarchinitiative.hpotextmining.gui.controllers.*;
import com.github.monarchinitiative.hpotextmining.gui.resources.OptionalResources;
import com.github.monarchinitiative.hpotextmining.gui.util.UTF8Control;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import javafx.application.Platform;
import javafx.stage.Stage;
import ontologizer.io.obo.OBOParser;
import ontologizer.io.obo.OBOParserException;
import ontologizer.io.obo.OBOParserFileInput;
import ontologizer.ontology.Ontology;
import ontologizer.ontology.TermContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Named;
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

    public static final String HP_OBO_PROPERTY = "hp.obo.path";

    public static final String HP_OBO_URL = "hp.obo.url";

    public static final String BIOLARK_URL_PROPERTY = "biolark.url";

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


        // Path to 'application.properties' file, where the App properties are expected to be defined
        bind(File.class)
                .annotatedWith(Names.named("propertiesFilePath"))
                .toInstance(new File(getCodeHomedir(), APPLICATION_PROPERTIES_FILENAME));

        // ---- CONTROLLERS ----
        bind(Main.class).in(Singleton.class);

        bind(OntologyTree.class).in(Singleton.class);

        bind(Configure.class).in(Singleton.class);

        bind(Present.class).in(Singleton.class);

        bind(PropertyManager.class).in(Singleton.class);
        // ---- CONTROLLERS ----
    }


    @Provides
    @Singleton
    private OptionalResources optionalServices(Properties properties) {
        OptionalResources optionalResources = new OptionalResources();

        String ontologyPath = properties.getProperty(HP_OBO_PROPERTY);
        if (ontologyPath != null) {
            if (new File(properties.getProperty(HP_OBO_PROPERTY)).isFile()) {
                try {
                    OBOParser parser = new OBOParser(new OBOParserFileInput(ontologyPath),
                            OBOParser.PARSE_DEFINITIONS);
                    LOGGER.info(parser.doParse());
                    TermContainer termContainer = new TermContainer(parser.getTermMap(), parser.getFormatVersion(), parser
                            .getDate());
                    optionalResources.setOntology(Ontology.create(termContainer));
                } catch (IOException | OBOParserException e) {
                    LOGGER.warn(e);
                    optionalResources.setOntology(null);
                    properties.setProperty(HP_OBO_PROPERTY, null);
                }
            } else {
                LOGGER.warn("Invalid path to HP obo file: {}", ontologyPath);
                optionalResources.setOntology(null);
                properties.setProperty(HP_OBO_PROPERTY, null);
            }
        }

        return optionalResources;
    }


    @Provides
    private HPOMiner hpoMiner(Properties properties) throws MalformedURLException {
        return new BiolarkHPOMiner(new URL(properties.getProperty(BIOLARK_URL_PROPERTY)));
    }


    @Provides
    @Singleton
    private Properties properties(@Named("propertiesFilePath") File propertiesPath) {
        Properties properties = new Properties();
        if (propertiesPath.isFile()) {
            try {
                LOGGER.info("Loading app properties from {}", propertiesPath.getAbsolutePath());
                properties.load(new FileReader(propertiesPath));
            } catch (IOException e) {
                LOGGER.warn(e);
            }
        } else {
            try {
                URL propertiesUrl = Main.class.getResource("/" + APPLICATION_PROPERTIES_FILENAME);
                LOGGER.info("Loading bundled app properties from {}", propertiesUrl.getPath());
                properties.load(Main.class.getResourceAsStream("/" + APPLICATION_PROPERTIES_FILENAME));
            } catch (IOException e) {
                LOGGER.warn(e);
            }
        }
        return properties;
    }


    /**
     * Get path to directory where the code (classes or JAR file) is located.
     *
     * @return {@link Optional} of {@link File} path of the directory with code. Empty, if the <code>UTF-8</code> is
     * not supported or if the classes are not being loaded from file
     */
    @Provides
    @Named("codeHomeDir")
    private File getCodeHomedir() {
        File codehomedir = new File(Play.class.getProtectionDomain().getCodeSource().getLocation().getFile())
                .getParentFile();
        if (codehomedir.exists() || codehomedir.mkdirs())
            return codehomedir;

        LOGGER.fatal("Could not figure out path to parent directory of the JAR file");
        Platform.exit();
        return null; // shouldn't get here, but we need to return something
    }


}
