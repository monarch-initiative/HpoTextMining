package org.monarchinitiative.application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;

/**
 * This class has control over independent windows (instances of {@link Stage}) in the application. It holds a reference
 * to owner {@link Window} (parent class of {@link Stage}).
 * <p> FXML file and {@link DialogController} are used to initialize window.
 * <p>
 * Created by Daniel Danis on 6/19/17.
 */
public class FXMLDialog extends Stage {


    private FXMLDialog(DialogController controller, URL fxml, Window owner, StageStyle style, Modality modality) {
        super(style);
        initOwner(owner);
        initModality(modality);
        FXMLLoader loader = new FXMLLoader(fxml);
        try {
            loader.setControllerFactory(param -> controller);
            controller.setDialog(this);
            setScene(new Scene(loader.load()));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Builder for {@link FXMLDialog} instances.
     */
    public static class FXMLDialogBuilder {

        private URL fxml = null;
        private DialogController controller = null;
        private Window owner = null;
        private StageStyle stageStyle = StageStyle.DECORATED;
        private Modality modality = Modality.WINDOW_MODAL;

        /**
         * Set path to FXML file that is describing the layout of dialog's elements. <b>REQUIRED</b>
         * @param fxml URL with path to the FXML file.
         * @return
         */
        public FXMLDialogBuilder setFXML(URL fxml) {
            this.fxml = fxml;
            return this;
        }

        /**
         * Set controller which will be used to control dialog's elements. <b>REQUIRED</b>
         * @param controller instance of {@link DialogController} to be used as controller.
         * @return {@link FXMLDialogBuilder}
         */
        public FXMLDialogBuilder setDialogController(DialogController controller) {
            this.controller = controller;
            return this;
        }

        /**
         * Set the owner of this dialog. Defaults to null, if unset.
         * @param owner instance of {@link Window} to be used as owner.
         * @return {@link FXMLDialogBuilder}
         */
        public FXMLDialogBuilder setOwner(Window owner) {
            this.owner = owner;
            return this;
        }

        /**
         * Set stage style of the dialog. Defaults to StageStyle.DECORATED, if unset.
         * @param style {@link StageStyle} to be used for the dialog.
         * @return {@link FXMLDialogBuilder}
         */
        public FXMLDialogBuilder setStageStyle(StageStyle style) {
            this.stageStyle = style;
            return this;
        }

        /**
         * Set modality of the dialog. Defaults to Modality.WINDOW_MODAL, if unset.
         * @param modality {@link Modality} to be used for the dialog.
         * @return {@link FXMLDialogBuilder}
         */
        public FXMLDialogBuilder setModality(Modality modality) {
            this.modality = modality;
            return this;
        }

        /**
         * Check that the required arguments (fxml URL, controller) have been set and build dialog instance.
         * @return new {@link FXMLDialog} instance.
         */
        public FXMLDialog build() {
            if (fxml == null || controller == null) {
                throw new IllegalStateException(String.format("Unable to create FXMLDialog with fxml = %s, controller = %s",
                        fxml, controller));
            }
            return new FXMLDialog(controller, fxml, owner, stageStyle, modality);
        }
    }


    /**
     * This method constructs {@link Parent} object using given FXML file and controller class which can be used as a
     * part of the {@link Scene} graph. Use it if constructed {@link Parent} will <em>NOT</em> be used as an independent
     * window but only as a part of existing {@link Scene}.
     * <p>
     * If you want to create whole new window create instance of {@link FXMLDialog} instead.
     * <p>
     *
     * @param fxml       url to FXML file from which returned {@link Parent} is about to be created.
     * @param controller object which will be used as a controller for created {@link Parent}.
     * @return created {@link Parent} object.
     */
    public static Parent loadParent(Object controller, URL fxml) throws IOException {
        return loadParent(controller, fxml, new HashSet<>());
    }

    /**
     * This method constructs {@link Parent} object using given FXML file and controller class which can be used as a
     * part of the {@link Scene} graph. Use it if constructed {@link Parent} will <em>NOT</em> be used as an independent
     * window but only as a part of existing {@link Scene}.
     * <p>
     * If you want to create whole new window create instance of {@link FXMLDialog} instead.
     * <p>
     *
     * @param fxml       url to FXML file from which returned {@link Parent} is about to be created.
     * @param controller object which will be used as a controller for created Parent.
     * @param cssPath    string URLs linking to the stylesheets to use with this Parent's contents.
     * @return created {@link Parent} object.
     */
    public static Parent loadParent(Object controller, URL fxml, Collection<String> cssPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(fxml);
        loader.setController(controller);
        Parent parent = loader.load();
        parent.getStylesheets().addAll(cssPath);
        return parent;
    }

}
