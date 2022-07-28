package com.findGender.findGender_Rest_API.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Controller
public class FindingGenderController {

    @RequestMapping("/getGenderDetails")
    public @ResponseBody
    JsonObject getGenderDetails(String genderName) throws IOException {
	
        JsonObject jsonObject = new JsonObject();
        jsonObject = findGender(genderName);
        String data = jsonObject.toString();
        data = data.replaceAll("^\"|\"$", "");
        StringTokenizer jsonTokenizer = new StringTokenizer(data,",");
        String internalData[];
        String expectedGenderFindingOutput = null;
        while (jsonTokenizer.hasMoreTokens()) {  
        	expectedGenderFindingOutput = jsonTokenizer.nextToken();
        	internalData = StringUtils.split(expectedGenderFindingOutput,":");
        	System.out.println(internalData[0]+internalData[1]);
        	if (internalData[0].substring(2,internalData[0].length()-1).equalsIgnoreCase("name")) {
        		jsonObject.addProperty("name", internalData[1].substring(1,internalData[1].length()-1));
        		
        	}
        	if (internalData[0].substring(1,internalData[0].length()-1).equalsIgnoreCase("gender")) {
        		jsonObject.addProperty("gender", internalData[1].substring(1,internalData[1].length()-1));
        	}
        	if (internalData[0].substring(1,internalData[0].length()-1).equalsIgnoreCase("probability")) {
        		jsonObject.addProperty("probability", internalData[1]);
        	}
        	if (internalData[0].substring(1,internalData[0].length()-1).equalsIgnoreCase("count")) {
        		jsonObject.addProperty("count", internalData[1].substring(0,internalData[1].length()-1));
        	}

        }

        jsonObject.addProperty("data", data);
    	

       

        return jsonObject;
    }

    private JsonObject findGender(String genderName) throws IOException {

        String data = null;
        StringBuilder responseData = new StringBuilder();
        JsonObject jsonObject = null;

        URL url = null;

        url = new URL("https://api.genderize.io?name=" + genderName);
        
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        int responseCode = con.getResponseCode();

        System.out.println("\nSending 'GET' request to URL : " + url);
//        System.out.println("Response Code : " + responseCode);

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {


            String line;

            while ((line = in.readLine()) != null) {
                responseData.append(line);
            }

            jsonObject = new Gson().fromJson(responseData.toString(), JsonObject.class);

        }
        //System.out.println(data);
        return jsonObject;
    }
}