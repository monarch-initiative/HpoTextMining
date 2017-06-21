package org.monarchinitiative.hpotextmining.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.monarchinitiative.hpotextmining.application.ApplicationConfigTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.*;

/**
 * Tests of {@link ConfigureController}.
 * Created by Daniel Danis on 6/20/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfigTest.class)
public class ConfigureControllerTest extends ApplicationTest {

    @Autowired
    private ConfigureController controller;

    private Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ConfigureView.fxml"));
        loader.setController(controller);
        Parent p = loader.load();
        scene = new Scene(p);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void text_input() throws Exception {
        String expected = "Hey ya!";
        clickOn("#contentTextArea").write(expected);
        assertEquals(expected, controller.getContentText());
        Button analyze = (Button) scene.getRoot().lookup("#analyzeButton");
        assertTrue(analyze.isDisabled());
    }

    @Test
    public void pmid_input() throws Exception {
        String expected = "42";
        clickOn("#pmidTextField").write(expected);
        assertEquals(expected, controller.getPMID());
        Button analyzeButton = (Button) scene.getRoot().lookup("#analyzeButton");
        assertTrue(analyzeButton.isDisabled());
    }

    @Test
    public void disabled_buttons() throws Exception {
        String content = "Hey ya!";
        String pmid = "42";
        clickOn("#contentTextArea").write(content);
        clickOn("#pmidTextField").write(pmid);
        Button analyzeButton = (Button) scene.getRoot().lookup("#analyzeButton");
        assertFalse(analyzeButton.isDisabled());
    }
}