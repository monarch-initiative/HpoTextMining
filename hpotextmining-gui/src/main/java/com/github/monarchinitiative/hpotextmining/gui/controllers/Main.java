package com.github.monarchinitiative.hpotextmining.gui.controllers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ComparisonChain;
import com.google.inject.Injector;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import ontologizer.ontology.Term;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.1
 * @since 0.2
 */
@Singleton
public final class Main {

    private static final Logger LOGGER = LogManager.getLogger();

    private final Configure configure;

    private final Present present;

    private final OntologyTree ontologyTree;

    private final Stage mainWindow;

    @FXML
    public ScrollPane rightScrollPane;

    @FXML
    public StackPane textMiningStackPane;

    @FXML
    private TableView<PhenotypeTerm> hpoTermsTableView;

    @FXML
    public Button removeButton;

    @Inject
    private Injector injector;

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
    public Main(Configure configure, Present present, OntologyTree ontologyTree, @Named("mainWindow") Stage mainWindow) {
        this.configure = configure;
        this.present = present;
        this.ontologyTree = ontologyTree;
        this.mainWindow = mainWindow;
    }


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

        configure.setQueryHook(text -> {
            present.setQueryText(text);
            textMiningStackPane.getChildren().clear();
            textMiningStackPane.getChildren().add(presentParent);
        });

        present.setResultHook(terms -> {
            textMiningStackPane.getChildren().clear();
            textMiningStackPane.getChildren().add(configureParent);
            hpoTermsTableView.getItems().addAll(terms);

        });
        present.setFocusToTermHook(ontologyTree::focusOnTerm);

        if (configureParent != null) {
            textMiningStackPane.getChildren().add(configureParent);
        }
    }


    @FXML
    void exitMenuItemAction() {
        Platform.exit();
    }


    @FXML
    void aboutMenuItemAction() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sorry");
        alert.setHeaderText("Sorry");
        alert.setContentText("About menu is not yet implemented");
        alert.show();
    }


    @FXML
    void setResourcesMenuItemAction() {
        try {
            Stage stage = new Stage();
            stage.initOwner(mainWindow);
            Parent root = FXMLLoader.load(Main.class.getResource("PropertyManager.fxml"),
                    injector.getInstance(ResourceBundle.class), new JavaFXBuilderFactory(), injector::getInstance);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            LOGGER.warn(e);
        }
    }


    @FXML
    void exportPhenopacketMenuItemAction() {
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


    public Set<PhenotypeTerm> getPhenotypeTerms() {
        return new HashSet<>(hpoTermsTableView.getItems());
    }

    /**
     * This class is a POJO containing attributes of HPO terms.
     *
     * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
     * @version 0.1.0
     * @since 0.1
     */
    public static final class PhenotypeTerm {

        private String hpoId, name, definition;

        private boolean present;


        @JsonCreator
        public PhenotypeTerm(
                @JsonProperty("hpoId") String hpoId,
                @JsonProperty("name") String name,
                @JsonProperty("definition") String definition,
                @JsonProperty("present") boolean present) {
            this.hpoId = hpoId;
            this.name = name;
            this.definition = definition;
            this.present = present;
        }


        public PhenotypeTerm(Term term, boolean present) {
            this.hpoId = term.getIDAsString();
            this.name = term.getName().toString();
            this.definition = (term.getDefinition() == null) ? "" : term.getDefinition().toString();
            this.present = present;
        }


        @JsonGetter
        public boolean isPresent() {
            return present;
        }


        public void setPresent(boolean present) {
            this.present = present;
        }


        @JsonGetter
        public String getHpoId() {
            return hpoId;
        }


        public void setHpoId(String hpoId) {
            this.hpoId = hpoId;
        }


        @JsonGetter
        public String getName() {
            return name;
        }


        public void setName(String name) {
            this.name = name;
        }


        @JsonGetter
        public String getDefinition() {
            return definition;
        }


        public void setDefinition(String definition) {
            this.definition = definition;
        }


        @Override
        public int hashCode() {
            int result = getHpoId().hashCode();
            result = 31 * result + getName().hashCode();
            result = 31 * result + (getDefinition() != null ? getDefinition().hashCode() : 0);
            result = 31 * result + (isPresent() ? 1 : 0);
            return result;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PhenotypeTerm that = (PhenotypeTerm) o;

            if (present != that.present) return false;
            if (!hpoId.equals(that.hpoId)) return false;
            if (!name.equals(that.name)) return false;
            return definition.equals(that.definition);
        }


        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("PhenotypeTerm{");
            sb.append("hpoId='").append(hpoId).append('\'');
            sb.append(", name='").append(name).append('\'');
            sb.append(", definition='").append(definition).append('\'');
            sb.append(", present=").append(present);
            sb.append('}');
            return sb.toString();
        }


        /**
         * Comparator for sorting terms by their IDs.
         *
         * @return {@link Comparator} of {@link PhenotypeTerm} objects.
         */
        public static Comparator<PhenotypeTerm> comparatorByHpoID() {
            return (l, r) -> ComparisonChain.start().compare(l.getHpoId(), r.getHpoId()).result();
        }
    }
}
