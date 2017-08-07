package org.monarchinitiative.hpotextmining.application;

import com.genestalker.springscreen.core.DialogController;
import com.genestalker.springscreen.core.FXMLDialog;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import ontologizer.ontology.Ontology;
import ontologizer.ontology.Term;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.hpotextmining.model.PhenotypeTerm;
import org.monarchinitiative.hpotextmining.model.SimpleTextMiningResult;
import org.monarchinitiative.hpotextmining.model.TextMiningResult;
import org.monarchinitiative.hpotextmining.util.WidthAwareTextFields;

import java.net.URL;
import java.util.*;

/**
 * This class acts as the controller of the main dialog window of the HPO text-mining analysis. It contains multiple
 * subparts: <ul><li><b>text-mining pane</b> - place where user submits a query text that is mined for HPO terms. The
 * results of analysis are presented in the same pane</li><li><b>summary pane</b> - contains field for manual lookup of
 * HPO terms which offers autocompletion and table displaying terms that will be returned as results.</li></ul>
 */
public class HPOAnalysisController implements DialogController {

    private static final Logger log = LogManager.getLogger();

    private final ConfigureController configureController;

    private final PresentController presentController;

    private final Ontology ontology;

    private Parent configurePane;

    private Parent presentPane;

    private FXMLDialog dialog;


    /**
     * Temporary copy for storing already present PhenotypeTerms until the controller GUI elements are initialized by
     * the FXML loader. The content is moved into the TableView in {@link #initialize(URL, ResourceBundle)} method.
     */
    private Set<PhenotypeTerm> terms = new HashSet<>();

    /**
     * Map of term names to term IDs.
     */
    private Map<String, String> labels = new HashMap<>();

    @FXML
    private AnchorPane treeViewAnchorPane;

    @FXML
    private StackPane textMiningStackPane;

    @FXML
    private TextField addTermTextField;

    @FXML
    private CheckBox notObservedCheckBox;

    @FXML
    private Button addTermButton;

    @FXML
    private Button removeTermButton;

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

    public HPOAnalysisController(Ontology ontology, URL textMiningServer, String pmid, Set<PhenotypeTerm> terms) {
        this.ontology = ontology;
        this.configureController = new ConfigureController(textMiningServer, pmid);
        this.presentController = new PresentController(ontology);
        this.terms.addAll(terms);

        configureController.setSignal(signal -> {
            switch (signal) {
                case DONE:
                    presentController.setResults(configureController.getJsonResponse(), configureController.getText());
                    textMiningStackPane.getChildren().clear();
                    textMiningStackPane.getChildren().add(presentPane);
                    break;
                case FAILED:
                    log.warn("Sorry, text mining analysis failed."); // TODO - improve cancellation & failed handling
                    break;
                case CANCELLED:
                    log.warn("Text mining analysis cancelled");
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
                    log.warn("Sorry, text mining analysis failed."); // TODO - improve cancellation & failed handling
                    break;
                case CANCELLED:
                    log.warn("Text mining analysis cancelled");
                    break;
            }
        });
    }

    @Override
    public void setDialog(FXMLDialog dialog) {
        this.dialog = dialog;
    }

    StackPane getTextMiningStackPane() {
        return textMiningStackPane;
    }

    /**
     * Get set of {@link PhenotypeTerm} that are the results of this analysis.
     *
     * @return
     */
    public Set<PhenotypeTerm> getPhenotypeTerms() {
        return new HashSet<>(hpoTermsTableView.getItems());
    }

    /**
     * Add a set of {@link PhenotypeTerm} that should be inserted into tableview upon initialization of the widget.
     *
     * @param phenotypeTerms
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
     * The end of analysis. Close the main dialog window.
     */
    @FXML
    void okButtonAction() {
        dialog.close();
    }

    /**
     * Add term that has been entered into textfield into the table.
     */
    @FXML
    void addTermButtonAction() {
        String id = labels.get(addTermTextField.getText());
        if (id != null) {
            Term term = ontology.getTerm(id);
            hpoTermsTableView.getItems().add(new PhenotypeTerm(term, !notObservedCheckBox.isSelected()));
            addTermTextField.clear();
            notObservedCheckBox.setSelected(false);
        }
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
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hpoIdTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getHpoId()));
        hpoNameTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getName()));
        observedTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper((cdf.getValue().isPresent()) ? "YES"
                : "NOT"));
        definitionTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getDefinition()));

        hpoTermsTableView.getItems().addAll(terms);
        terms = null;

        configurePane = FXMLDialog.loadParent(configureController, getClass().getResource("/fxml/ConfigureView.fxml"));
        presentPane = FXMLDialog.loadParent(presentController, getClass().getResource("/fxml/PresentView.fxml"));

        textMiningStackPane.getChildren().add(configurePane);

        ontology.getTermMap().forEach(term -> labels.put(term.getName().toString(), term.getIDAsString()));
        WidthAwareTextFields.bindWidthAwareAutoCompletion(addTermTextField, labels.keySet());
    }

    /**
     * @return
     */
    public TextMiningResult getResults() {
        return new SimpleTextMiningResult(configureController.getPmid(), getPhenotypeTerms());
    }

}
