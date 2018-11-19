package com.github.monarchinitiative.hpotextmining.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class MonarchSciGraphResultTest {

    private static String jsonString;
    private static String jsonListString;
    private static ObjectMapper mapper = new ObjectMapper();
    @BeforeClass
    public static void setup() {
        jsonString = "{\"token\":{\"id\":\"MP:0000751\",\"categories\":[\"Phenotype\"],\"terms\":[\"myopathy\"]},\"start\":50,\"end\":58}";
        jsonListString = new BufferedReader(new InputStreamReader(MonarchSciGraphResultTest.class.getClassLoader().getResourceAsStream("sciGraphJsonResponse.txt"))).lines().collect(Collectors.joining());
    }

    @Test
    public void simpleTest() throws Exception{

        MonarchSciGraphResult result = mapper.readValue(jsonString, MonarchSciGraphResult.class);
        assertNotNull(result);
        assertEquals(result.getToken().getId(), "MP:0000751");
        assertEquals(result.getStart(), 50);
        assertEquals(result.getEnd(), 58);
    }

    @Test
    public void jsonList() throws Exception {
        MonarchSciGraphResult[] results = mapper.readValue(jsonListString, MonarchSciGraphResult[].class);
        assertEquals(results.length, 66);
    }

    @Test
    public void testCompare() throws Exception {
        MonarchSciGraphResult[] results = mapper.readValue(jsonListString, MonarchSciGraphResult[].class);
        List<MonarchSciGraphResult> l = Arrays.asList(results);
        Collections.shuffle(l);
        //l.forEach(o -> System.out.print(o.getStart() + "\t"));
        //System.out.println("");
        Collections.sort(l);
        for (int i = 0; i < l.size() - 1; i++) {
            assertTrue(l.get(i).getStart() <= l.get(i + 1).getStart());
        }
        //l.forEach(o -> System.out.print(o.getStart() + "\t"));
    }



}