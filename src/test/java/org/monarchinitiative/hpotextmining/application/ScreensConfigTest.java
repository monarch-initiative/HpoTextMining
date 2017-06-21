package org.monarchinitiative.hpotextmining.application;

import javafx.stage.Stage;
import org.monarchinitiative.hpotextmining.controllers.ConfigureController;
import org.monarchinitiative.hpotextmining.controllers.PresentController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * Created by Daniel Danis on 6/20/17.
 */
@Configuration
public class ScreensConfigTest {

    private Stage window;

    public void setWindow(Stage window) {
        this.window = window;
    }

    @Bean
    @Lazy
    public PresentController presentController() {
        return new PresentController();
    }

    @Bean
    @Lazy
    public FXMLDialog presentDialog() {
        return new FXMLDialog.FXMLDialogBuilder()
                .setDialogController(presentController())
                .setFXML(getClass().getResource("/fxml/PresentView.fxml"))
                .setOwner(window)
                .build();
    }

    @Bean
    @Lazy
    public ConfigureController configureController() {
        return new ConfigureController();
    }

    @Bean
    @Lazy
    public FXMLDialog configureDialog() {
        return new FXMLDialog.FXMLDialogBuilder()
                .setDialogController(configureController())
                .setFXML(getClass().getResource("/fxml/ConfigureView.fxml"))
                .setOwner(window)
                .build();
    }

}
