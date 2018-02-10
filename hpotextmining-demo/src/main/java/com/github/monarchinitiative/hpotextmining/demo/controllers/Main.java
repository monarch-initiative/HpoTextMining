package com.github.monarchinitiative.hpotextmining.demo.controllers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.monarchinitiative.hpotextmining.core.miners.HPOMiner;
import com.google.common.collect.ComparisonChain;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import ontologizer.ontology.Ontology;
import ontologizer.ontology.Term;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is the main controller of the app. It contains several subparts, such as ontology tree view, a table for
 * accepted HPO terms and an area for the text mining.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.1
 * @since 0.2
 */
public final class Main {

    private static final Logger LOGGER = LogManager.getLogger();

    // --------- sub-controllers ---------
    private final Configure configure;

    private final Present present;

    private final OntologyTree ontologyTree;
    // --------- sub-controllers ---------

    private final ObjectProperty<Ontology> ontology = new SimpleObjectProperty<>(this, "ontology", null);

    @FXML
    public StackPane textMiningStackPane;

    @FXML
    public Button removeButton;

    @FXML
    public StackPane leftStackPane;

    @FXML
    private TableView<PhenotypeTerm> hpoTermsTableView;

    private Parent configureParent, presentParent;

    @FXML
    private TableColumn<PhenotypeTerm, String> hpoIdTableColumn;

    @FXML
    private TableColumn<PhenotypeTerm, String> hpoNameTableColumn;

    @FXML
    private TableColumn<PhenotypeTerm, String> observedTableColumn;

    @FXML
    private TableColumn<PhenotypeTerm, String> definitionTableColumn;


    public Main(HPOMiner miner) {
        this.present = new Present(miner);
        this.configure = new Configure();
        this.ontologyTree = new OntologyTree();
    }


    public Ontology getOntology() {
        return ontology.get();
    }


    public void setOntology(Ontology ontology) {
        this.ontology.set(ontology);
    }


    public ObjectProperty<Ontology> ontologyProperty() {
        return ontology;
    }


    public void initialize() {
        try {
            FXMLLoader loader = new FXMLLoader(Configure.class.getResource("Configure.fxml"));
            loader.setControllerFactory(clazz -> configure);
            configureParent = loader.load();

            loader = new FXMLLoader(Present.class.getResource("Present.fxml"));
            loader.setControllerFactory(param -> present);
            presentParent = loader.load();

            loader = new FXMLLoader(OntologyTree.class.getResource("OntologyTree.fxml"));
            loader.setControllerFactory(clazz -> ontologyTree);
            leftStackPane.getChildren().add(loader.load());
        } catch (IOException e) {
            LOGGER.warn(e);
        }

        // initialize behaviour of columns of the TableView
        hpoIdTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getHpoId()));
        hpoNameTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getName()));
        observedTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper((cdf.getValue().isPresent()) ? "YES"
                : "NOT"));
        definitionTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getDefinition()));

        // bind Ontology properties of sub-controllers to the Ontology of this controller
        configure.ontologyProperty().bind(ontology);
        present.ontologyProperty().bind(ontology);
        ontologyTree.ontologyProperty().bind(ontology);

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
    public void removeButtonAction() {
        hpoTermsTableView.getItems().remove(hpoTermsTableView.getSelectionModel().getSelectedIndex());
    }


    @FXML
    public void okButtonAction() {
        hpoTermsTableView.getItems().forEach(System.out::println);
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
