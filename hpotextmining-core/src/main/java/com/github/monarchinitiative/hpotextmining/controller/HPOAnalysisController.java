package com.github.monarchinitiative.hpotextmining.controller;

import com.genestalker.springscreen.core.DialogController;
import com.genestalker.springscreen.core.FXMLDialog;
import com.github.monarchinitiative.hpotextmining.TextMiningResult;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import ontologizer.ontology.Ontology;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.github.monarchinitiative.hpotextmining.model.PhenotypeTerm;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Consumer;

/**
 * This class acts as the controller of the main dialog window of the HPO dialog. The dialog window can be divided
 * into these subparts:
 * <ul>
 * <li><b>ontology tree pane</b> - tree hierarchy of the ontology is presented here</li>
 * <li><b>text-mining pane</b> - place where user submits a query text that is mined for HPO terms. The results
 * of the analysis are then presented in the same pane</li>
 * <li><b>approved terms</b> - table with the approved terms</li>
 * </ul>
 * <p>
 * The terms which are inside the <em>approved terms</em> table will be wrapped into {@link TextMiningResult} and
 * returned as results after closing the dialog.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.1.0
 * @since 0.1
 */
@Deprecated
public class HPOAnalysisController implements DialogController {

    private static final Logger LOGGER = LogManager.getLogger();

    private ConfigureController configureController;

    private PresentController presentController;

    private OntologyTreeController ontologyTreeController;

    private Parent configurePane;

    private Parent presentPane;

    private FXMLDialog dialog;

    /**
     * Temporary copy for storing already present PhenotypeTerms until the controller GUI elements are initialized by
     * the FXML loader. The content is moved into the TableView in {@link #initialize(URL, ResourceBundle)} method.
     */
    private Set<PhenotypeTerm> terms = new HashSet<>();

    @FXML
    private StackPane treeViewStackPane;

    @FXML
    private StackPane textMiningStackPane;

    /**
     * Clicking on this button invokes action described in {@link #removeButtonAction()}.
     */
    @FXML
    private Button removeButton;

    /**
     * This table contains accepted {@link PhenotypeTerm}s.
     */
    @FXML
    private TableView<PhenotypeTerm> hpoTermsTableView;

    @FXML
    private TableColumn<PhenotypeTerm, String> hpoIdTableColumn;

    @FXML
    private TableColumn<PhenotypeTerm, String> hpoNameTableColumn;

    @FXML
    private TableColumn<PhenotypeTerm, String> observedTableColumn;

    @FXML
    private TableColumn<PhenotypeTerm, String> definitionTableColumn;


    /**
     * Create the controller.
     *
     * @param ontology         {@link Ontology} containing HPO terms
     * @param textMiningServer {@link URL} pointing to server that performs text-mining analysis
     */
    public HPOAnalysisController(Ontology ontology, URL textMiningServer) {

        this.configureController = new ConfigureController(textMiningServer);

        this.presentController = new PresentController(ontology);

        // This action will be run after user approves a PhenotypeTerm in the ontologyTreePane
        Consumer<PhenotypeTerm> addHook = (ph -> hpoTermsTableView.getItems().add(ph));
        this.ontologyTreeController = new OntologyTreeController(ontology, addHook);

        configureController.setSignal(signal -> {
            switch (signal) {
                case DONE:
                    presentController.setResults(configureController.getJsonResponse(), configureController.getText());
                    textMiningStackPane.getChildren().clear();
                    textMiningStackPane.getChildren().add(presentPane);
                    break;
                case FAILED:
                    LOGGER.warn("Sorry, text mining analysis failed."); // TODO - improve cancellation & failed handling
                    break;
                case CANCELLED:
                    LOGGER.warn("Text mining analysis cancelled");
                    break;
            }
        });

        presentController.setSignal(signal -> {
            switch (signal) {
                case DONE:
                    addPhenotypeTerms(presentController.getApprovedTerms());
                    textMiningStackPane.getChildren().clear();
                    textMiningStackPane.getChildren().add(configurePane);
                    break;
                case FAILED:
                    LOGGER.warn("Sorry, text mining analysis failed."); // TODO - improve cancellation & failed handling
                    break;
                case CANCELLED:
                    LOGGER.warn("Text mining analysis cancelled");
                    break;
            }
        });

        // clicking on the highlighted text chunk will show the corresponding item in the ontology TreeView
        presentController.setFocusToTermHook(ontologyTreeController::focusOnTerm);
    }


    public String getPmid() {
        return configureController.getPmid();
    }


    public void setPmid(String pmid) {
        configureController.setPmid(pmid);
    }


    @Override
    public void setDialog(FXMLDialog dialog) {
        this.dialog = dialog;
    }


    /**
     * Get set of approved {@link PhenotypeTerm}s.
     *
     * @return new {@link Set} containing the approved terms
     */
    public Set<PhenotypeTerm> getPhenotypeTerms() {
        return new HashSet<>(hpoTermsTableView.getItems());
    }


    /**
     * Add a set of {@link PhenotypeTerm} which will be displayed in table at the bottom of the dialog.
     *
     * @param phenotypeTerms {@link Set} of {@link PhenotypeTerm}s
     */
    public void addPhenotypeTerms(Set<PhenotypeTerm> phenotypeTerms) {
        if (hpoTermsTableView == null) { // make a temporary copy until processing of GUI elements by FXMLLoader
            terms.addAll(phenotypeTerms);
        } else {
            phenotypeTerms.stream()
                    .filter(t -> !hpoTermsTableView.getItems().contains(t))
                    .forEach(t -> hpoTermsTableView.getItems().add(t));
        }
    }


    /**
     * The end of analysis. Close the dialog window.
     */
    @FXML
    void okButtonAction() {
        dialog.close();
    }


    /**
     * Remove selected {@link PhenotypeTerm} from TableView.
     */
    @FXML
    void removeButtonAction() {
        hpoTermsTableView.getItems().removeAll(hpoTermsTableView.getSelectionModel().getSelectedItems());
    }


    /**
     * {@inheritDoc}
     *
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // initialize behaviour of columns of the TableView
        hpoIdTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getHpoId()));
        hpoNameTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getName()));
        observedTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper((cdf.getValue().isPresent()) ? "YES"
                : "NOT"));
        definitionTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getDefinition()));

        // populate sub-components of this dialog
        configurePane = FXMLDialog.loadParent(configureController, getClass().getResource("/fxml/ConfigureView.fxml"));
        presentPane = FXMLDialog.loadParent(presentController, getClass().getResource("/fxml/PresentView.fxml"));
        textMiningStackPane.getChildren().add(configurePane);

        Parent ontologyViewPane = FXMLDialog.loadParent(ontologyTreeController,
                getClass().getResource("/fxml/OntologyTreeView.fxml"));
        treeViewStackPane.getChildren().add(ontologyViewPane);

        if (!terms.isEmpty())
            hpoTermsTableView.getItems().addAll(terms);
    }


    /**
     * Enum used by {@link ConfigureController} and {@link PresentController} to signalize progres & status.
     */
    enum Signal {
        DONE,
        CANCELLED,
        FAILED
    }

}
