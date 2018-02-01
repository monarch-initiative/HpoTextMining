package com.github.monarchinitiative.hpotextmining.gui.controllers;

import com.github.monarchinitiative.hpotextmining.model.HPOMiner;
import com.github.monarchinitiative.hpotextmining.model.PhenotypeTerm;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.1
 * @since 0.2
 */
public final class Present {

    @FXML
    public WebView webView;

    @FXML
    public VBox yesTermsVBox;

    @FXML
    public VBox notTermsVBox;

    @Inject
    private HPOMiner miner;

    private Consumer<Set<PhenotypeTerm>> hook;


    public void setHook(Consumer<Set<PhenotypeTerm>> hook) {
        this.hook = hook;
    }


    @FXML
    void addTermsButtonAction() {
        hook.accept(new HashSet<>()); // TODO - create real logic that will
    }


    void setQueryText(String query) {
        // TODO - create task here that will set the results after the query has been mined
    }
}
