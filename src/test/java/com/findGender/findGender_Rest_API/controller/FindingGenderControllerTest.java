package com.findGender.findGender_Rest_API.controller;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StringUtils;

import com.findGender.findGender_Rest_API.controller.FindingGenderController;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class FindingGenderControllerTest {
    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new FindingGenderController()).build();
    }


    @Test
    public void testGenderFindingDetails() throws Exception {        
        //expected
        String expectedData = null;
        StringBuilder responseData = new StringBuilder();
        JsonObject expectedJsonObject = null;
        JsonArray expectedJsonArray = null;
        String expectedName = null,expectedGender = null,expectedProbability = null,expectedCount = null;
        URL url = new URL("https://api.genderize.io?name=rachel");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {

            String line;

            while ((line = in.readLine()) != null) {
                responseData.append(line);
            }

            expectedJsonObject = new Gson().fromJson(responseData.toString(), JsonObject.class);
            expectedData = expectedJsonObject.toString().replaceAll("^\"|\"$", "");
            StringTokenizer jsonTokenizer = new StringTokenizer(expectedData,",");
            String internalData[];
            String expectedGenderFindingOutput = null;
            
            while (jsonTokenizer.hasMoreTokens()) {  
            	expectedGenderFindingOutput = jsonTokenizer.nextToken();
            	internalData = StringUtils.split(expectedGenderFindingOutput,":");
            	if (internalData[0].substring(2,internalData[0].length()-1).equalsIgnoreCase("name")) {
            		expectedName = internalData[1].substring(1,internalData[1].length()-1);          		
            	}
            	if (internalData[0].substring(1,internalData[0].length()-1).equalsIgnoreCase("gender")) {
            		expectedGender = internalData[1].substring(1,internalData[1].length()-1);
            	}
            	if (internalData[0].substring(1,internalData[0].length()-1).equalsIgnoreCase("probability")) {
            		expectedProbability = internalData[1];
            	}
            	if (internalData[0].substring(1,internalData[0].length()-1).equalsIgnoreCase("count")) {
            		expectedCount = internalData[1].substring(0,internalData[1].length()-1);
            	}
            }
            
            System.out.println(expectedName + expectedGender + expectedProbability + expectedCount);
        }

        //actual
        MvcResult result = mockMvc.perform(get("/getGenderDetails?genderName=rachel"))
                .andReturn();
        String recievedResponse = result.getResponse().getContentAsString();
        JsonObject actualJsonObject = new Gson().fromJson(recievedResponse, JsonObject.class);
        String actualName = actualJsonObject.get("name").toString();
        actualName = actualName.replaceAll("^\"|\"$", "");
        String actualGender = actualJsonObject.get("gender").toString();
        actualGender = actualGender.replaceAll("^\"|\"$", "");
        String actualProbability = actualJsonObject.get("probability").toString();
        actualProbability = actualProbability.replaceAll("^\"|\"$", "");
        String actualCount = actualJsonObject.get("count").toString();
        actualCount = actualCount.replaceAll("^\"|\"$", "");
        assertEquals(expectedName, actualName);
        assertEquals(expectedGender, actualGender);
        assertEquals(expectedProbability, actualProbability);
        assertEquals(expectedCount, actualCount);
    }

}
