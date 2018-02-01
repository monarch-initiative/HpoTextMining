package com.github.monarchinitiative.hpotextmining.gui;

import com.github.monarchinitiative.hpotextmining.gui.controllers.Configure;
import com.github.monarchinitiative.hpotextmining.gui.controllers.OntologyTree;
import com.github.monarchinitiative.hpotextmining.gui.controllers.Present;
import com.github.monarchinitiative.hpotextmining.gui.util.UTF8Control;
import com.github.monarchinitiative.hpotextmining.model.HPOMiner;
import com.github.monarchinitiative.hpotextmining.model.HPOMinerImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scope;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import javafx.stage.Stage;
import ontologizer.io.obo.OBOParser;
import ontologizer.io.obo.OBOParserException;
import ontologizer.io.obo.OBOParserFileInput;
import ontologizer.ontology.Ontology;
import ontologizer.ontology.TermContainer;

import java.io.IOException;
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

        bind(HPOMiner.class)
                .to(HPOMinerImpl.class)
                .in(Scopes.SINGLETON);

        // ---- CONTROLLERS ----
        bind(OntologyTree.class)
                .toInstance(new OntologyTree());

        bind(Configure.class)
                .toInstance(new Configure());

        bind(Present.class)
                .toInstance(new Present());
        // ---- CONTROLLERS ----
    }


    @Provides
    private Ontology ontology() throws IOException, OBOParserException {
        // TODO - solve ontology path setting
        OBOParser parser = new OBOParser(new OBOParserFileInput(getClass().getResource("/HP.obo").getPath()),
                OBOParser.PARSE_DEFINITIONS);
        String result = parser.doParse();
        TermContainer termContainer = new TermContainer(parser.getTermMap(), parser.getFormatVersion(), parser
                .getDate());
        return Ontology.create(termContainer);
    }

}
