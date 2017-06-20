package org.monarchinitiative.application;

import javafx.fxml.Initializable;
import javafx.stage.Stage;

/**
 * Every class that acts as a controller of an independent window ({@link Stage}) needs to implement this
 * interface.
 * <p>
 * Created by Daniel Danis on 6/19/17.
 */
public interface DialogController extends Initializable {

    /**
     * Inject reference to an instance of {@link FXMLDialog} (subclass of {@link Stage}) to be able to access the
     * window's properties.
     *
     * @param dialog The {@link FXMLDialog} instance which represents an independent window.
     */
    void setDialog(FXMLDialog dialog);

}
