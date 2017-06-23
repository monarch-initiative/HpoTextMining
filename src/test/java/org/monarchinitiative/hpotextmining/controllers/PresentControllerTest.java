package org.monarchinitiative.hpotextmining.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.monarchinitiative.hpotextmining.application.ApplicationConfigTest;
import org.monarchinitiative.hpotextmining.model.DataBucket;
import org.monarchinitiative.hpotextmining.model.Term;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testfx.framework.junit.ApplicationTest;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;


/**
 * Tests of {@link PresentController} class.
 * Created by Daniel Danis on 6/19/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfigTest.class)
public class PresentControllerTest extends ApplicationTest {

    private Scene scene;

    @Autowired
    private PresentController controller;

    @Autowired
    private DataBucket dataBucket;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PresentView.fxml"));
        loader.setController(controller);
        Parent p = loader.load();
        scene = new Scene(p);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void select_yes_checkboxes() throws Exception {
        List<Node> yesBoxes = ((VBox) scene.getRoot().lookup("#yesTermsVBox")).getChildren();
        clickOn(yesBoxes.get(0)).clickOn(yesBoxes.get(3));
        Set<Term> resultSet = controller.getApprovedYesTerms();
        assertEquals(2, resultSet.size());
        clickOn(yesBoxes.get(0));
        resultSet = controller.getApprovedYesTerms();
        assertEquals(1, resultSet.size());
        clickOn(yesBoxes.get(0)).clickOn(yesBoxes.get(3));
        resultSet = controller.getApprovedYesTerms();
        assertEquals(1, resultSet.size());
        Term result = resultSet.iterator().next();
        assertEquals("HP:0001367", result.getId());
        assertEquals("Abnormal joint morphology", result.getLabel());
        assertEquals(4, result.getSynonyms().size());
    }

    @Test
    public void select_not_checkboxes() throws Exception {
        List<Node> notBoxes = ((VBox) scene.getRoot().lookup("#notTermsVBox")).getChildren();
        clickOn(notBoxes.get(0));
        Set<Term> resultSet = controller.getApprovedNotTerms();
        assertEquals(1, resultSet.size());
    }

    @Test
    public void colorizeHTML() throws Exception {
        String actual = PresentController.colorizeHTML(dataBucket.getIntermediateResults(), dataBucket.getMinedText());
        String expected = "<html><style> .tooltip { position: relative; display: inline-block; border-bottom: 1px dotted black; }.tooltip .tooltiptext { visibility: hidden; width: 230px; background-color: #555; color: #fff; text-align: center; border-radius: 6px; padding: 5px 0; position: absolute; z-index: 1; bottom: 125%; left: 50%; margin-left: -60px; opacity: 0; transition: opacity 1s; }.tooltip .tooltiptext::after { content: \"\"; position: absolute; top: 100%; left: 50%; margin-left: -5px; border-width: 5px; border-style: solid; border-color: #555 transparent transparent transparent; }.tooltip:hover .tooltiptext { visibility: visible; opacity: 1;}</style><body><h2>BioLark concept recognition</h2><p>Our case is a 24-year-old male born to consanguineous Yemeni parents. He was healthy at birth and as a baby he achieved normal developmental milestones. By three years of age, he started\n" +
                "to have difficulties during walking and developed progressive <b><span class=\"tooltip\" style=\"color:red\">knee deformities<span class=\"tooltiptext\">HP:0002815\n" +
                "Abnormality of the knee</span></span></b>. Rapidly over several years, he started to develop progressive symmetric <b><span class=\"tooltip\" style=\"color:red\">joint pain<span class=\"tooltiptext\">HP:0002829\n" +
                "Arthralgia</span></span></b>, stiffness and <b><span class=\"tooltip\" style=\"color:red\">swelling<span class=\"tooltiptext\">HP:0011855\n" +
                "Pharyngeal edema</span></span></b>.\n" +
                "The first <b><span class=\"tooltip\" style=\"color:red\">joint involved<span class=\"tooltiptext\">HP:0001367\n" +
                "Abnormal joint morphology</span></span></b> were the knees followed by hips, elbows and hand joints. The <b><span class=\"tooltip\" style=\"color:red\">pain<span class=\"tooltiptext\">HP:0012531\n" +
                "Pain</span></span></b> involved almost all joints, but more severe in hips and lower back. The patient was used to take\n" +
                "non-steroidal anti-inflammatory drugs (NSAIDs) irregularly in the case of severe <b><span class=\"tooltip\" style=\"color:red\">pain<span class=\"tooltiptext\">HP:0012531\n" +
                "Pain</span></span></b>, but he has never been on steroid therapy. There were no symptoms of numbness or tingling in the\n" +
                "extremities and there was no <b><span class=\"tooltip\" style=\"color:red\">hepatosplenomegaly<span class=\"tooltiptext\">HP:0001433\n" +
                "Hepatosplenomegaly</span></span></b>. He exhibited a flexed posture in the trunk and extremities and <b><span class=\"tooltip\" style=\"color:red\">abnormal gait<span class=\"tooltiptext\">HP:0001288\n" +
                "Gait disturbance</span></span></b> (Figure 1A and supplemental video S1). We found <b><span class=\"tooltip\" style=\"color:red\">enlargement\n" +
                "of joints<span class=\"tooltiptext\">HP:0003037\n" +
                "Enlarged joints</span></span></b>, which were more prominent in the interphalangeal, elbow and knees joints (Figure 1 A, B), but there were no signs of inflammation such as tenderness or redness. Movements of\n" +
                "all joints were extremely restricted, including neck, spine, shoulder, elbow, wrist, knee, and ankle and interphalangeal joints of hands and feet. The mental status, vision, hearing\n" +
                "and speech were normal.\n</p></body></html>";
        /* I have adjusted the template in the PresentController class to avoid problems with
        * overlapping hits. My correction causes some double whitespaces to be inserted, which do not affect the
        * final HTML presentation (although it is not elegant). Therefore, the following removes any series of
        * multiple white spaces before the test.
         */
        actual = actual.replaceAll("\\s{2,}", " ").trim();
        assertEquals(expected, actual);
    }


}