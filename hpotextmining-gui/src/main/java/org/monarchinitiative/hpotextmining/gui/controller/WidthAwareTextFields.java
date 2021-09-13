package org.monarchinitiative.hpotextmining.gui.controller;

import javafx.scene.control.TextField;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.util.Collection;

class WidthAwareTextFields {

    /**
     * Create autocompletion binding between given {@link TextField} instance and Collection of possible suggestions.
     * Additionally, bind the minWidthProperty of suggestion box to widthProperty of textField.
     *
     * @param textField           TextField to which the suggestions will be offered.
     * @param possibleSuggestions Collection of all possible suggestions.
     * @param <T>                 type
     * @return the binding object
     */
    static <T> AutoCompletionBinding<T> bindWidthAwareAutoCompletion(TextField textField,
                                                                     Collection<T> possibleSuggestions) {
        AutoCompletionBinding<T> k = TextFields.bindAutoCompletion(textField, possibleSuggestions);
        k.minWidthProperty().bind(textField.widthProperty());
        return k;
    }

}
