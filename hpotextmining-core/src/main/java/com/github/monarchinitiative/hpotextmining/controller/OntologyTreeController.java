package com.github.monarchinitiative.hpotextmining.controller;

import com.genestalker.springscreen.core.DialogController;
import com.genestalker.springscreen.core.FXMLDialog;
import com.github.monarchinitiative.hpotextmining.model.PhenotypeTerm;
import com.github.monarchinitiative.hpotextmining.util.WidthAwareTextFields;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import ontologizer.ontology.Ontology;
import ontologizer.ontology.Term;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
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
 * @version 0.1.0
 * @since 0.1
 */
public class OntologyTreeController implements DialogController {

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Approved {@link PhenotypeTerm} is submitted here.
     */
    private final Consumer<PhenotypeTerm> addHook;

    /**
     * Ontology object containing {@link Term}s and their relationships.
     */
    private Ontology ontology;

    private FXMLDialog dialog;

    /**
     * Map of term names to term IDs.
     */
    private Map<String, String> labels = new HashMap<>();

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
     * Create the controller.
     *
     * @param ontology {@link Ontology} object containing the term hierarchy and relationships.
     * @param addHook  {@link Consumer} for approved {@link PhenotypeTerm}s.
     */
    public OntologyTreeController(Ontology ontology, Consumer<PhenotypeTerm> addHook) {
        this.ontology = ontology;
        this.addHook = addHook;
    }


    /**
     * Expand & scroll to the term selected in the search text field.
     */
    @FXML
    private void goButtonAction() {
        String id = labels.get(searchTextField.getText());
        if (id != null) {
            expandUntilTerm(ontology.getTerm(id));
            searchTextField.clear();
        }
    }


    /**
     * Create {@link PhenotypeTerm} from the currently selected {@link #ontologyTreeView} element considering also
     * {@link #notPresentCheckBox}.
     */
    @FXML
    private void addButtonAction() {
        if (ontologyTreeView.getSelectionModel().getSelectedItem() != null) {
            PhenotypeTerm phenotypeTerm = new PhenotypeTerm(
                    ontologyTreeView.getSelectionModel().getSelectedItem().getValue(), !notPresentCheckBox.isSelected());
            addHook.accept(phenotypeTerm);
        }
        notPresentCheckBox.setSelected(false);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setDialog(FXMLDialog dialog) {
        this.dialog = dialog;
    }


    /**
     * Populate the {@link #ontologyTreeView} with the root {@link Term}s of provided {@link Ontology}. Initialize
     * other JavaFX elements.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // populate the TreeView with top-level elements from ontology hierarchy
        TreeItem<Term> root = new OntologyTreeController.TermTreeItem(ontology.getRootTerm());
        root.setExpanded(true);
        ontologyTreeView.setShowRoot(false);
        ontologyTreeView.setRoot(root);
        ontologyTreeView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> updateDescription(newValue));

        // create Map for lookup of the terms in the ontology based on their Name
        ontology.getTermMap().forEach(term -> labels.put(term.getName().toString(), term.getIDAsString()));
        WidthAwareTextFields.bindWidthAwareAutoCompletion(searchTextField, labels.keySet());

        // show intro message in the infoWebView
        infoWebEngine = infoWebView.getEngine();
        infoWebEngine.loadContent("<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>HPO tree browser</title></head>" +
                "<body><p>Click on HPO term in the tree browser to display additional information</p></body></html>");
    }


    /**
     * Focus on the HPO term with given ID if the term is contained in the ontology.
     *
     * @param termId String with HPO term id (e.g. HP:0002527 for Falls)
     */
    void focusOnTerm(String termId) {
        Term term = ontology.getTerm(termId);
        if (term == null) {
            LOGGER.warn("Unable to focus on term with id {} because it is not defined in the ontology", termId);
            return;
        }
        expandUntilTerm(term);
    }


    /**
     * Find the path from the root term to given {@link Term}, expand the tree and set the selection model of the
     * TreeView to the term position.
     *
     * @param term {@link Term} to be displayed
     */
    private void expandUntilTerm(Term term) {
        if (ontology.existsPath(ontology.getRootTerm().getID(), term.getID())) {
            // find root -> term path through the tree
            Stack<Term> termStack = new Stack<>();
            termStack.add(term);
            Set<Term> parents = ontology.getTermParents(term);
            while (parents.size() != 0) {
                Term parent = parents.iterator().next();
                termStack.add(parent);
                parents = ontology.getTermParents(parent);
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
            LOGGER.warn(String.format("Unable to find the path from %s to %s", ontology.getRootTerm(), term.getName()));
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

        String termID = String.format("%s:%07d", term.getID().getPrefix(), term.getID().id);
        String synonyms = (term.getSynonyms() == null) ? "" : Arrays.stream(term.getSynonyms()).map(Object::toString)
                .collect(Collectors.joining(", ")); // Synonyms
        String definition = (term.getDefinition() == null) ? "" : term.getDefinition().toString();

        String content = String.format(HTML_TEMPLATE, termID, term.getName(), synonyms, definition);
        infoWebEngine.loadContent(content);
    }


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
            Set<Term> children = ontology.getTermChildren(getValue());
            return children.size() == 0;
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
                Set<Term> children = ontology.getTermChildren(getValue());
                children.stream()
                        .sorted((l, r) -> l.getName().compareTo(r.getName()))
                        .map(OntologyTreeController.TermTreeItem::new)
                        .forEach(childrenList::add);
                super.getChildren().setAll(childrenList);
            }
            return super.getChildren();
        }

    }
}
