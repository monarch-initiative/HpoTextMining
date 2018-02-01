package com.github.monarchinitiative.hpotextmining.gui.controllers;

import com.github.monarchinitiative.hpotextmining.model.PhenotypeTerm;
import com.google.inject.Injector;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.ResourceBundle;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.1
 * @since 0.2
 */
@Singleton
public final class Main {

    private static final Logger LOGGER = LogManager.getLogger();

    @FXML
    public ScrollPane rightScrollPane;

    @FXML
    public StackPane textMiningStackPane;

    @FXML
    public TableView<PhenotypeTerm> hpoTermsTableView;

    @FXML
    public Button removeButton;

    @Inject
    private Configure configure;

    @Inject
    private Present present;

    @Inject
    private OntologyTree ontologyTree;

    private Parent configureParent, presentParent;

    @FXML
    private TableColumn<PhenotypeTerm, String> hpoIdTableColumn;

    @FXML
    private TableColumn<PhenotypeTerm, String> hpoNameTableColumn;

    @FXML
    private TableColumn<PhenotypeTerm, String> observedTableColumn;

    @FXML
    private TableColumn<PhenotypeTerm, String> definitionTableColumn;

    @Inject
    private Injector injector;

    public void initialize() {
        try {
            configureParent = FXMLLoader.load(Configure.class.getResource("Configure.fxml"),
                    injector.getInstance(ResourceBundle.class), new JavaFXBuilderFactory(), injector::getInstance);
            presentParent = FXMLLoader.load(Present.class.getResource("Present.fxml"),
                    injector.getInstance(ResourceBundle.class), new JavaFXBuilderFactory(), injector::getInstance);
        } catch (IOException e) {
            LOGGER.warn(e);
        }

        // initialize behaviour of columns of the TableView
        hpoIdTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getHpoId()));
        hpoNameTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getName()));
        observedTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper((cdf.getValue().isPresent()) ? "YES"
                : "NOT"));
        definitionTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getDefinition()));

        // the PhenotypeTerm approved by user will appear here after user clicks on 'Add' button in OntologyTree
        ontologyTree.setAddHook(term -> hpoTermsTableView.getItems().add(term));
        configure.setHook(text -> {
            present.setQueryText(text);
            textMiningStackPane.getChildren().clear();
            textMiningStackPane.getChildren().add(presentParent);
        });

        present.setHook(terms -> {
            System.out.println(terms);
            textMiningStackPane.getChildren().clear();
            textMiningStackPane.getChildren().add(configureParent);
        });
        if (configureParent != null) {
            textMiningStackPane.getChildren().add(configureParent);
        }
    }


    @FXML
    public void exitMenuItemAction() {
        Platform.exit();
    }


    @FXML
    public void aboutMenuItemAction() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sorry");
        alert.setHeaderText("Sorry");
        alert.setContentText("About menu is not yet implemented");
        alert.show();
    }


    @FXML
    public void setResourcesMenuItemAction() {
    }


    @FXML
    public void exportPhenopacketMenuItemAction() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sorry");
        alert.setHeaderText("Sorry");
        alert.setContentText("Phenopacket export is not yet implemented");
        alert.show();
    }


    @FXML
    public void removeButtonAction() {
    }


    @FXML
    public void okButtonAction() {
    }
}
