package org.monarchinitiative.application;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import org.monarchinitiative.controllers.ConfigureController;
import org.monarchinitiative.controllers.PresentController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Configuration of {@link FXMLDialog}s used in text-mining application.
 * IMPORTANT - dialogs and controllers must have lazy instantiation.
 * Created by Daniel Danis on 6/19/17.
 */
@Configuration
public class ScreensConfig {

    private Stage owner;

    public void setOwner(Stage owner) {
        this.owner = owner;
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
                .setOwner(owner)
                .build();
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
                .setOwner(owner)
                .build();
    }


    /**
     * Class full of utility methods which can be used to show {@link Alert} windows from various reasons. Following
     * properties can be specified when creating pop-up alert owner:
     * <ul>
     * <li>title of the newly created owner</li>
     * <li>header that is placed in a body of the owner</li>
     * <li>text that forms a content of the owner</li>
     * </ul>
     * Please keep these texts short because the area of pop-ups is small. Set content to null if you want the particular
     * to stay empty.
     * <p>
     * Inspiration gathered mainly from <a href="http://code.makery.ch/blog/javafx-dialogs-official/">here</a>.
     * </p>
     */
    public static class Alerts {

        /**
         * Use this alert dialog for presenting information to user.
         *
         * @param alertTitle   title of created information pop-up owner.
         * @param alertHeader  text to be used as header of the presented information.
         * @param alertContent text that forms the content of presented information.
         */
        public static void showInformationDialog(String alertTitle, String alertHeader, String alertContent) {
            Alert alert = getAlertWindow(alertTitle, alertHeader, alertContent, Alert.AlertType.INFORMATION);
            alert.showAndWait();
        }

        /**
         * Show warning to user.
         *
         * @param alertTitle   title of created warning pop-up owner.
         * @param alertHeader  header of warning, shown in the body of pop-up.
         * @param alertContent text of presented warning.
         */
        public static void showWarningDialog(String alertTitle, String alertHeader, String alertContent) {
            Alert alert = getAlertWindow(alertTitle, alertHeader, alertContent, Alert.AlertType.WARNING);
            alert.showAndWait();
        }

        /**
         * Show error to user.
         *
         * @param alertTitle   title of created error pop-up owner.
         * @param alertHeader  header of error, presented in the body of pop-up.
         * @param alertContent text of presented error.
         */
        public static void showErrorDialog(String alertTitle, String alertHeader, String alertContent) {
            Alert alert = getAlertWindow(alertTitle, alertHeader, alertContent, Alert.AlertType.ERROR);
            alert.showAndWait();
        }

        /**
         * Use this dialog to present an ugly Exception in visually pleasant way.
         *
         * @param alertTitle   title of created pop-up owner.
         * @param alertHeader  header of exception, presented in the body of pop-up.
         * @param alertContent text describing thrown exception.
         * @param ex           exception being presented.
         */
        public static void showExceptionDialog(String alertTitle, String alertHeader, String alertContent, Exception ex) {
            Alert alert = getAlertWindow(alertTitle, alertHeader, alertContent, Alert.AlertType.ERROR);

            // Create expandable Exception.
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();

            Label label = new Label("The exception stacktrace was:");
            TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);
            textArea.setWrapText(true);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(label, 0, 0);
            expContent.add(textArea, 0, 1);

            // Set expandable Exception into the dialog pane.
            alert.getDialogPane().setExpandableContent(expContent);
            alert.showAndWait();
        }

        /*
         * Prepare pop-up owner of specified type.
         */
        private static Alert getAlertWindow(String alertTitle, String alertHeader, String alertContent, Alert.AlertType type) {
            Alert alert = new Alert(type);
            alert.setTitle(alertTitle);
            alert.setHeaderText(alertHeader);
            alert.setContentText(alertContent);
            return alert;
        }

    }
}
