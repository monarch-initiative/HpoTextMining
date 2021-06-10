package org.monarchinitiative.hpotextmining.gui.controller;


import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.hpotextmining.core.miners.MinedTerm;
import org.monarchinitiative.hpotextmining.core.miners.SimpleMinedTerm;
import org.monarchinitiative.hpotextmining.core.miners.TermMiner;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests of {@link Configure} class.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @author <a href="mailto:aaron.zhangl@jax.org">Aaron Zhang</a>
 * @version 0.1.0
 * @since 0.1
 */
public class ConfigureTest extends ApplicationTest {

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final TermMiner miner = Mockito.mock(TermMiner.class);

    private Main.Signal result;

    /**
     * Tested controller.
     */
    private Configure controller;

    @BeforeAll
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
        Set<MinedTerm> terms = Set.of(t);
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