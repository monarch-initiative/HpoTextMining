package com.github.monarchinitiative.hpotextmining.gui.controller;


import com.github.monarchinitiative.hpotextmining.core.miners.MinedTerm;
import com.github.monarchinitiative.hpotextmining.core.miners.SimpleMinedTerm;
import com.github.monarchinitiative.hpotextmining.core.miners.TermMiner;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.testfx.framework.junit.ApplicationTest;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests of {@link com.github.monarchinitiative.hpotextmining.gui.controller.Configure} class.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @author <a href="mailto:aaron.zhangl@jax.org">Aaron Zhang</a>
 * @version 0.1.0
 * @since 0.1
 */
public class ConfigureTest extends ApplicationTest {

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private TermMiner miner;

    private Main.Signal result;

    /**
     * Tested controller.
     */
    private Configure controller;

    @BeforeClass
    public static void setUpBefore() throws Exception {
        // for headless GUI testing, set the "not.headless" system property to true or comment out if you want to see the
        // robot in action
        if (!Boolean.getBoolean("not.headless")) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("java.awt.headless", "true");
            System.setProperty("headless.geometry", "1200x760-32");
        }
    }

    /**
     * Test input to text area.
     *
     * @throws Exception bla
     */
    @Test
    public void testInputToTextArea() throws Exception {
        MinedTerm t = new SimpleMinedTerm(7, 11, "HP:0001945", true);
        Set<MinedTerm> terms = new HashSet<>(Collections.singletonList(t));
        Mockito.when(miner.doMining("Bla bla bla"))
                .thenReturn(terms);

        clickOn("#contentTextArea")
                .write("Bla bla bla")
                .clickOn("#analyzeButton");

        assertThat(controller.getTerms(), hasItem(t));
        assertThat(result, is(Main.Signal.DONE));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage stage) throws Exception {
        controller = new Configure(miner, executorService, s -> result = s);
        FXMLLoader loader = new FXMLLoader(Configure.class.getResource("Configure.fxml"));
        loader.setControllerFactory(clazz -> controller);
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }

}