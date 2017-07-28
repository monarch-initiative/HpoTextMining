package org.monarchinitiative.hpotextmining.application;

import javafx.stage.Stage;
import org.monarchinitiative.hpotextmining.controllers.ConfigureController;
import org.monarchinitiative.hpotextmining.controllers.PresentController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Class containing configuration of controller objects that are tested Created by Daniel Danis on 6/20/17.
 */
@Configuration
public class ScreensConfigTest {

    private Stage window;

    public void setWindow(Stage window) {
        this.window = window;
    }

    @Bean
    public PresentController presentController() {
        return new PresentController();
    }


    @Bean
    public ConfigureController configureController() {
        return new ConfigureController();
    }

}
