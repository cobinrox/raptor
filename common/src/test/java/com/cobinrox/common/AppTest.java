package com.cobinrox.common;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * Unit test for simple App.
 */
public class AppTest
        extends TestCase {


    public void testJsonParse() throws Throwable {

        String jsonString = "{\"F\":[\"+1\"],\"B\":[\"-1\"],\"L\":[\"+1\"],\"R\":[\"-1\"],\"FL\":[\"+1\",\"+1\"],\"FR\":[\"-1\",\"+1\"],\"BL\":[\"-1\",\"-1\"],\"BR\":[\"+1\",\"-1\"],\"T\":[\"+1\",\"-1\"]}";
        //String jsonString = "{\"FL\":[\"hi\"],\"BL\":[\"there\",\"you\"]}";
        List<String> fl = Utils.getStringsFromJsonArrayWithKey(jsonString, "FL");
        for (String s : fl)
            System.out.println(s);

        Properties p = new Properties();
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("test.properties");
        //p.load( new InputStreamReader(new FileInputStream(new File("test.properties"))));
        p.load(is);

        String jsonString2 = (String) p.getProperty("jsonString");
        assertEquals(jsonString, jsonString2);
        fl = Utils.getStringsFromJsonArrayWithKey(jsonString2, "FR");
        for (String s : fl)
            System.out.println(s);

        JSONParser jasonParser = new JSONParser();
        System.out.println("jsonparser");


    }

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        assertTrue(true);
    }
}
