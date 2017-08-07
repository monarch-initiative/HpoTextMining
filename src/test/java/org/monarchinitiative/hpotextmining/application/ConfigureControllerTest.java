package org.monarchinitiative.hpotextmining.application;

import com.genestalker.springscreen.core.FXMLDialog;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import org.junit.Test;
import org.loadui.testfx.GuiTest;

import static org.junit.Assert.*;

/**
 * Tests of {@link ConfigureController}. Created by Daniel Danis on 6/20/17.
 */
public class ConfigureControllerTest extends GuiTest {

    private ConfigureController controller;

    /**
     * Test the method that extracts inserted text from the TextField. If pmid is blank the analyze button is disabled.
     */
    @Test
    public void text_input() throws Exception {
        String expected = "Hey ya";
        click("#contentTextArea").type(expected);
        assertEquals(expected, controller.getText());
        Button analyze = find("#analyzeButton");
        assertTrue(analyze.isDisabled());
    }

    /**
     * Test getting inserted PMID. If text field is blank, the analyze button is disabled.
     */
    @Test
    public void pmid_input() throws Exception {
        String expected = "12";
        click("#pmidTextField").type(expected);
        assertEquals(expected, controller.getPmid());
        Button analyzeButton = find("#analyzeButton");
        assertTrue(analyzeButton.isDisabled());
    }

    /**
     * Test that only after entering both PMID and analyzed text the analysis is enabled.
     */
    @Test
    public void disabled_buttons() throws Exception {
        Button analyzeButton = find("#analyzeButton");
        String content = "Hey ya";
        String pmid = "12";
        click("#contentTextArea").type(content);
        assertTrue(analyzeButton.isDisabled());
        click("#pmidTextField").type(pmid);
        assertFalse(analyzeButton.isDisabled());
    }

    @Override
    protected Parent getRootNode() {
        controller = new ConfigureController(null, "");
        return FXMLDialog.loadParent(controller, getClass().getResource("/fxml/ConfigureView.fxml"));
    }
}