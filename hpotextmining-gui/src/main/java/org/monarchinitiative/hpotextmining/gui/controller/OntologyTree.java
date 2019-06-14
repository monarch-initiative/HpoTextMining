package org.monarchinitiative.hpotextmining.gui.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.TermSynonym;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * This class acts as the controller of the Pane on the left side of the main HPO dialog. The top part of the pane
 * presents HPO hierarchy as a tree using {@link TreeView}. Details of any selected HPO term are presented at
 * the bottom of the Pane.
 * <p>
 * User can either browse the ontology tree by expanding individual tree elements or jump to any term using a search
 * text field with autocompletion capabilities.
 * <p>
 * The selected term (either present of not) is added to the table using <em>Add</em> button at the bottom of the
 * Pane.
 * <p>
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @author <a href="mailto:aaron.zhangl@jax.org">Aaron Zhang</a>
 * @version 0.1.0
 * @since 0.1
 */
public class OntologyTree {

    private static final Logger LOGGER = LoggerFactory.getLogger(OntologyTree.class);

    /**
     * Ontology object containing {@link Term}s and their relationships.
     */
    private final Ontology ontology;


    /**
     * Approved {@link Main.PhenotypeTerm} is submitted here.
     */
    private final Consumer<Main.PhenotypeTerm> addHook;

    /**
     * Map of term names to term IDs.
     */
    private Map<String, TermId> labels = new HashMap<>();

    /**
     * Text field with autocompletion for jumping to a particular HPO term in the tree view.
     */
    @FXML
    private TextField searchTextField;

    /**
     * Clicking on this button will perform action described in {@link #goButtonAction()}.
     */
    @FXML
    private Button goButton;

    /**
     * Clicking on this button will perform action described in {@link #addButtonAction()}
     */
    @FXML
    private Button addButton;

    /**
     * Tree hierarchy of the ontology is presented here.
     */
    @FXML
    private TreeView<Term> ontologyTreeView;

    /**
     * WebView for displaying details of the Term that is selected in the {@link #ontologyTreeView}.
     */
    @FXML
    private WebView infoWebView;

    /**
     * WebEngine backing up the {@link #infoWebView}.
     */
    private WebEngine infoWebEngine;

    /**
     * User selects this CheckBox if the phenotype term is <em>NOT</em> present in the patient.
     */
    @FXML
    private CheckBox notPresentCheckBox;


    /**
     * @param ontology {@link Ontology} to be displayed here as a tree
     * @param addHook  {@link Consumer} of {@link Main.PhenotypeTerm}, an approved term will be submitted here
     */
    public OntologyTree(Ontology ontology, Consumer<Main.PhenotypeTerm> addHook) {
        this.ontology = ontology;
        this.addHook = addHook;
    }


    /**
     * Expand & scroll to the term selected in the search text field.
     */
    @FXML
    private void goButtonAction() {
        TermId id = labels.get(searchTextField.getText());
        if (id != null) {
            expandUntilTerm(ontology.getTermMap().get(id));
            searchTextField.clear();
        }
    }


    /**
     * Create {@link Main.PhenotypeTerm} from the currently selected {@link #ontologyTreeView} element considering also
     * {@link #notPresentCheckBox}.
     */
    @FXML
    private void addButtonAction() {
        TreeItem<Term> selected = ontologyTreeView.getSelectionModel().getSelectedItem();
        if (selected != null && addHook != null) {
            Main.PhenotypeTerm phenotypeTerm = new Main.PhenotypeTerm(selected.getValue(), !notPresentCheckBox.isSelected());
            addHook.accept(phenotypeTerm);
        }
        notPresentCheckBox.setSelected(false);
    }


    /**
     * Populate the {@link #ontologyTreeView} with the root {@link Term}s of provided {@link Ontology}. Initialize
     * other JavaFX elements.
     * <p>
     * {@inheritDoc}
     */
    public void initialize() {
        // make sure that the content & controls of the OntologyTree will be disabled if the ontology is not present
        String introHtmlMessage;
        if (ontology != null) {
            // populate the TreeView with top-level elements from ontology hierarchy
            TreeItem<Term> root = new OntologyTree.TermTreeItem(ontology.getTermMap().get(ontology.getRootTermId()));
            root.setExpanded(true);
            ontologyTreeView.setShowRoot(false);
            ontologyTreeView.setRoot(root);
            ontologyTreeView.getSelectionModel().selectedItemProperty()
                    .addListener((observable, oldValue, newValue) -> updateDescription(newValue));

            ontologyTreeView.setCellFactory(new Callback<TreeView<Term>, TreeCell<Term>>() {
                @Override
                public TreeCell<Term> call(TreeView<Term> param) {
                    return new TreeCell<Term>() {
                        @Override
                        public void updateItem(Term term, boolean empty) {
                            super.updateItem(term, empty);
                            if (empty) {
                                setText(null);
                            } else {
                                setText(term.getName());
                            }
                        }
                    };
                }
            });

            // create Map for lookup of the terms in the ontology based on their Name
            ontology.getTermMap().values().forEach(term -> labels.putIfAbsent(term.getName(), term.getId()));
            WidthAwareTextFields.bindWidthAwareAutoCompletion(searchTextField, labels.keySet());

            // show intro message in the infoWebView
            introHtmlMessage = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>HPO tree browser</title></head>" +
                    "<body><p>Click on HPO term in the tree browser to display additional information</p></body></html>";
        } else {
            goButton.setDisable(true);
            searchTextField.setDisable(true);
            ontologyTreeView.setDisable(true);
            notPresentCheckBox.setDisable(true);
            addButton.setDisable(true);

            introHtmlMessage = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>HPO tree browser</title></head>" +
                    "<body><p>Ontology is not available so the functions are disabled.</p></body></html>";
        }
        infoWebEngine = infoWebView.getEngine();
        infoWebEngine.loadContent(introHtmlMessage);
    }


    /**
     * Focus on the HPO term with given ID if the term is contained in the ontology.
     *
     * @param term {@link Term} on which we should focus
     */
    void focusOnTerm(Term term) {
        expandUntilTerm(term);
    }


    /**
     * Find the path from the root term to given {@link Term}, expand the tree and set the selection model of the
     * TreeView to the term position.
     *
     * @param term {@link Term} to be displayed
     */
    private void expandUntilTerm(Term term) {
        if (OntologyAlgorithm.existsPath(ontology, term.getId(), ontology.getRootTermId())) {
            // find root -> term path through the tree
            Stack<Term> termStack = new Stack<>();
            termStack.add(term);
            Set<TermId> parents = ontology.getParentTermIds(term.getId()); //getTermParents(term);
            while (parents.size() != 0) {
                TermId parent = parents.iterator().next();
                termStack.add(ontology.getTermMap().get(parent));
                parents = ontology.getParentTermIds(parent);
            }

            // expand tree nodes in top -> down direction
            List<TreeItem<Term>> children = ontologyTreeView.getRoot().getChildren();
            termStack.pop(); // get rid of 'All' node which is hidden
            TreeItem<Term> target = ontologyTreeView.getRoot();
            while (!termStack.empty()) {
                Term current = termStack.pop();
                for (TreeItem<Term> child : children) {
                    if (child.getValue().equals(current)) {
                        child.setExpanded(true);
                        target = child;
                        children = child.getChildren();
                        break;
                    }
                }
            }
            ontologyTreeView.getSelectionModel().select(target);
            ontologyTreeView.scrollTo(ontologyTreeView.getSelectionModel().getSelectedIndex());
        } else {
            LOGGER.warn("Unable to find the path from {} to {}", ontology.getRootTermId(), term.getId());
        }
    }


    /**
     * Get currently selected Term. Used in tests.
     *
     * @return {@link TermTreeItem} that is currently selected
     */
    TermTreeItem getSelectedTerm() {
        return (ontologyTreeView.getSelectionModel().getSelectedItem() == null) ? null
                : (TermTreeItem) ontologyTreeView.getSelectionModel().getSelectedItem();
    }


    /**
     * Update content of the {@link #infoWebView} with currently selected {@link Term}.
     *
     * @param treeItem currently selected {@link TreeItem} containing {@link Term}
     */
    private void updateDescription(TreeItem<Term> treeItem) {
        if (treeItem == null)
            return;
        Term term = treeItem.getValue();
        String HTML_TEMPLATE = "<!DOCTYPE html>" +
                "<html lang=\"en\"><head><meta charset=\"UTF-8\"><title>HPO tree browser</title></head>" +
                "<body>" +
                "<p><b>Term ID:</b> %s</p>" +
                "<p><b>Term Name:</b> %s</p>" +
                "<p><b>Synonyms:</b> %s</p>" +
                "<p><b>Definition:</b> %s</p>" +
                "</body></html>";

        String termID = term.getId().getValue();
        String synonyms = (term.getSynonyms() == null) ? "" : term.getSynonyms().stream()
                .map(TermSynonym::getValue)
                .collect(Collectors.joining(", ")); // Synonyms

        String definition = (term.getDefinition() == null) ? "" : term.getDefinition();

        String content = String.format(HTML_TEMPLATE, termID, term.getName(), synonyms, definition);
        infoWebEngine.loadContent(content);
    }

    public StringProperty observableSearchText() {return this.searchTextField.textProperty();}


    /**
     * Inner class that defines a bridge between hierarchy of {@link Term}s and {@link TreeItem}s of the
     * {@link TreeView}.
     */
    class TermTreeItem extends TreeItem<Term> {

        /**
         * List used for caching of the children of this term
         */
        private ObservableList<TreeItem<Term>> childrenList;


        /**
         * Default & only constructor for the TreeItem.
         *
         * @param term {@link Term} that is represented by this TreeItem
         */
        TermTreeItem(Term term) {
            super(term);
        }


        /**
         * Check that the {@link Term} that is represented by this TreeItem is a leaf term as described below.
         * <p>
         * {@inheritDoc}
         */
        @Override
        public boolean isLeaf() {
            return OntologyAlgorithm.getChildTerms(ontology, getValue().getId(), false).size() == 0;
        }


        /**
         * Get list of children of the {@link Term} that is represented by this TreeItem.
         * <p>
         * {@inheritDoc}
         */
        @Override
        public ObservableList<TreeItem<Term>> getChildren() {
            if (childrenList == null) {
                LOGGER.debug(String.format("Getting children for term %s", getValue().getName()));
                childrenList = FXCollections.observableArrayList();
                Set<Term> children = OntologyAlgorithm.getChildTerms(ontology, getValue().getId(), false).stream()
                        .map(ontology.getTermMap()::get)
                        .collect(Collectors.toSet());

                children.stream()
                        .sorted(Comparator.comparing(Term::getName))
                        .map(OntologyTree.TermTreeItem::new)
                        .forEach(childrenList::add);
                super.getChildren().setAll(childrenList);
            }
            return super.getChildren();
        }

    }
}
