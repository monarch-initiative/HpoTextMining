package org.monarchinitiative.hpotextmining.application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.*;

/**
 * Tests of {@link ConfigureController}. Created by Daniel Danis on 6/20/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = HPOTermsAnalysisConfigTest.class)
public class ConfigureControllerTest extends ApplicationTest {

    @Autowired
    private ConfigureController controller;

    @Autowired
    private Parent configureDialog;

    @Test
    public void text_input() throws Exception {
        String expected = "Hey ya";
        clickOn("#contentTextArea").write(expected);
        assertEquals(expected, controller.getText());
        Button analyze = lookup("#analyzeButton").query();
        assertTrue(analyze.isDisabled());
    }

//    @Test
//    @Ignore
//    public void pmid_input() throws Exception {
//        String expected = "42";
//        click("#pmidTextField").type(expected);
//        Button analyzeButton = find("#analyzeButton");
//        assertTrue(analyzeButton.isDisabled());
//    }

    @Test
    public void disabled_buttons() throws Exception {
        String content = "Hey ya";
        String pmid = "42";
        clickOn("#contentTextArea").write(content);
        clickOn("#pmidTextField").write(pmid);
        Button analyzeButton = lookup("#analyzeButton").query();
        assertFalse(analyzeButton.isDisabled());
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ConfigureView.fxml"));
        loader.setControllerFactory(param -> controller);
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }
}