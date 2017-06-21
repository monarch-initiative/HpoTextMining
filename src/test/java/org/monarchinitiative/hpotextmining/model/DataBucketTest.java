package org.monarchinitiative.hpotextmining.model;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.monarchinitiative.hpotextmining.application.ApplicationConfigTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Tests pertaining to {@link DataBucket} class.
 * Created by Daniel Danis on 6/19/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfigTest.class)
public class DataBucketTest {

    @Autowired
    private DataBucket dataBucket;

    private static String jsonPayload;

    @BeforeClass
    public static void setUpBefore() throws Exception {
        File file = new File(DataBucketTest.class.getResource("/payload.json").toURI());
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        int read = fis.read(data);
        fis.close();
        if (read != file.length()) {
            throw new IllegalStateException("Whoops");
        }
        jsonPayload = new String(data);
    }


    @Test
    public void setJsonResult() throws Exception {
        dataBucket.setJsonResult(jsonPayload);
        Set<BiolarkResult> results = dataBucket.getIntermediateResults();

        List<BiolarkResult> sortedResults = results.stream().sorted(BiolarkResult.compareByStart()).collect(Collectors.toList());
        BiolarkResult first = sortedResults.get(0);
        // Set of synonyms are not being used in equals method, empty set is ok.
        Term first_result = new Term("HP:0002815", "Abnormality of the knee", new HashSet<>());

        assertEquals(249, first.getStart());
        assertEquals(265, first.getEnd());
        assertEquals(16, first.getLength());
        assertEquals("knee deformities", first.getOriginal_text());
        assertEquals("HPO", first.getSource());
        assertEquals(first_result, first.getTerm());
        assertFalse(first.isNegated());

        BiolarkResult seventh = sortedResults.get(6);
        Term seventh_result = new Term("HP:0001433", "Hepatosplenomegaly", new HashSet<>());

        assertEquals(775, seventh.getStart());
        assertEquals(793, seventh.getEnd());
        assertEquals(18, seventh.getLength());
        assertEquals("hepatosplenomegaly", seventh.getOriginal_text());
        assertEquals("HPO", seventh.getSource());
        assertEquals(seventh_result, seventh.getTerm());
        assertTrue(seventh.isNegated());
    }
}