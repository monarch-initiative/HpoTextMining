package com.github.monarchinitiative.hpotextmining.controller;

import javafx.stage.Stage;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.net.URL;

/**
 * Tests of {@link ConfigureController} class.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.1.0
 * @since 0.1
 */
@Ignore
public class ConfigureControllerTest extends ApplicationTest {

    private static URL textMiningUrl;

    /**
     * Tested controller.
     */
//    private ConfigureController controller;


    /**
     * Test setting PMID using setter.
     *
     * @throws Exception bla
     */
    @Test
    public void testSettingPmid() throws Exception {
//        assertThat(controller.getPmid(), is(""));
//        controller.setPmid("16543");
//        assertThat(controller.getPmid(), is("16543"));
    }


    /**
     * Test typing to pmid text field.
     *
     * @throws Exception bla
     */
    @Test
    public void testTypingPmid() throws Exception {
//        clickOn("#pmidTextField").write("131");
//        assertThat(controller.getPmid(), is("131"));
    }


    /**
     * Test input to text area.
     *
     * @throws Exception bla
     */
    @Test
    public void testInputToTextArea() throws Exception {
//        clickOn("#contentTextArea").write("Hey ya!");
//        assertThat(controller.getText(), is("Hey ya!"));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage stage) throws Exception {
//        controller = new ConfigureController(textMiningUrl);
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TestConfigureView.fxml"));
//        loader.setControllerFactory(param -> controller);
//        Scene scene = new Scene(loader.load());
//        stage.setScene(scene);
//        stage.show();
    }


    @BeforeClass
    public static void beforeClassSetUp() throws Exception {
        textMiningUrl = new URL("http://phenotyper.monarchinitiative.org:5678/cr/annotate");
    }
}