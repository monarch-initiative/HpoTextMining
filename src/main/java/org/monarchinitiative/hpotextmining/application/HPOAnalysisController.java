package org.monarchinitiative.hpotextmining.application;

import com.genestalker.springscreen.core.DialogController;
import com.genestalker.springscreen.core.FXMLDialog;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import ontologizer.ontology.Ontology;
import ontologizer.ontology.Term;
import org.monarchinitiative.hpotextmining.model.PhenotypeTerm;
import org.monarchinitiative.hpotextmining.util.WidthAwareTextFields;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.*;

/**
 * This class acts as the controller of the main dialog window of the HPO text-mining analysis. It contains multiple
 * subparts: <ul><li><b>text-mining pane</b> - place where user submits a query text that is mined for HPO terms. The
 * results of analysis are presented in the same pane</li><li><b>summary pane</b> - contains field for manual lookup of
 * HPO terms which offers autocompletion and table displaying terms that will be returned as results.</li></ul>
 */
public class HPOAnalysisController implements DialogController {

    private FXMLDialog dialog;

    private StringProperty pmid = new SimpleStringProperty(this, "pmid", "");

    @Autowired
    private Ontology ontology;

    private Set<PhenotypeTerm> terms = new HashSet<>();

    /**
     * Map of term names to term IDs.
     */
    private Map<String, String> labels = new HashMap<>();

    private HPOAnalysisScreenConfig screenConfig;

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

    public HPOAnalysisController(HPOAnalysisScreenConfig screenConfig) {
        this.screenConfig = screenConfig;
    }

    private static void cleanup() {
        System.gc();
    }

    @Override
    public void setDialog(FXMLDialog dialog) {
        this.dialog = dialog;
    }

    StackPane getTextMiningStackPane() {
        return textMiningStackPane;
    }

    public String getPmid() {
        return pmid.get();
    }

    public void setPmid(String pmid) {
        this.pmid.set(pmid);
    }

    public StringProperty pmidProperty() {
        return pmid;
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
        cleanup();
    }

    /**
     * Add term into the table.
     */
    @FXML
    void addTermButtonAction() {
        String id = labels.get(addTermTextField.getText());
        if (id != null) {
            Term term = ontology.getTerm(id);
            Set<PhenotypeTerm> terms = new HashSet<>();
            terms.add(new PhenotypeTerm(term, !notObservedCheckBox.isSelected()));
            addPhenotypeTerms(terms);
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
        textMiningStackPane.getChildren().add(screenConfig.configureDialog());
        if (terms != null) {
            hpoTermsTableView.getItems().addAll(terms);
        }

        hpoIdTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getHpoId()));
        hpoNameTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getName()));
        observedTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper((cdf.getValue().isPresent()) ? "YES"
                : "NOT"));
        definitionTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getDefinition()));

        ontology.getTermMap().forEach(term -> labels.put(term.getName().toString(), term.getIDAsString()));
        WidthAwareTextFields.bindWidthAwareAutoCompletion(addTermTextField, labels.keySet());
    }
}
