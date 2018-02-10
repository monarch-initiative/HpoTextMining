package com.github.monarchinitiative.hpotextmining.gui.controllers;

import com.github.monarchinitiative.hpotextmining.gui.TestModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.testfx.api.FxToolkit;

import java.util.ResourceBundle;
import java.util.concurrent.TimeoutException;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.1
 * @since 0.2
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({MainTest.class})
public class GuiceSuiteBase {

    private static Injector INJECTOR;

    static {
        try {
            INJECTOR = Guice.createInjector(new TestModule(FxToolkit.registerPrimaryStage()));
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    private static final ResourceBundle BUNDLE = INJECTOR.getInstance(ResourceBundle.class);


    public static ResourceBundle getBundle() {
        return BUNDLE;
    }


    public static Injector getInjector() {
        return INJECTOR;
    }

}
