package org.monarchinitiative.hpotextmining.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.net.URL;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests of {@link ConfigureController} class.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.1.0
 * @since 0.1
 */
public class ConfigureControllerTest extends ApplicationTest {

    private static URL textMiningUrl;

    /**
     * Tested controller.
     */
    private ConfigureController controller;


    /**
     * Test input to pmid text field.
     *
     * @throws Exception bla
     */
    @Test
    public void testPmid() throws Exception {
        clickOn("#pmidTextField").write("131");
        assertThat(controller.getPmid(), is("131"));
    }


    /**
     * Test input to text area.
     *
     * @throws Exception bla
     */
    @Test
    public void testInputToTextArea() throws Exception {
        clickOn("#contentTextArea").write("Hey ya!");
        assertThat(controller.getText(), is("Hey ya!"));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage stage) throws Exception {
        controller = new ConfigureController(textMiningUrl);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TestConfigureView.fxml"));
        loader.setControllerFactory(param -> controller);
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }


    @BeforeClass
    public static void beforeClassSetUp() throws Exception {
        textMiningUrl = new URL("http://phenotyper.monarchinitiative.org:5678/cr/annotate");
    }
}