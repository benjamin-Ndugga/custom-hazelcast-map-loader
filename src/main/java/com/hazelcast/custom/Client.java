package com.hazelcast.custom;

import java.io.IOException;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 *
 * @author Benjamin E Ndugga
 */
public class Client {

    public static void main(String[] args) throws FileNotFoundException {

        fetchAllCountryCodes();

    }

    private static void fetchAllCountryCodes() throws FileNotFoundException {

        //connnect to the HZ-Instance  
        HazelcastInstance hazelcastInstance = HazelcastClient.newHazelcastClient();
        IMap<Object, Object> map_country_codes = hazelcastInstance.getMap("country_codes");

        FileReader fileReader = new FileReader("country-details.json");
        JsonObject jsonObject = new JsonParser().parse(fileReader).getAsJsonObject();

        JsonArray arr = jsonObject.getAsJsonArray("result");

        System.out.println("Array-Size: " + arr.size());

        for (int i = 0; i < arr.size(); i++) {

            String country = arr.get(i).getAsJsonObject().get("name").getAsString();
            String country_code = arr.get(i).getAsJsonObject().get("code").getAsString();
            System.out.println(country + " | " + country_code);
            map_country_codes.put(country, country_code);

        }

        hazelcastInstance.shutdown();
    }

    private static void fetchAllCountries() {
        try {

            HttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet("https://restcountries.eu/rest/v2/name/uganda");

            //httpGet.setHeader("Accept", "application/json");
            String response = httpClient.execute(httpGet, (ClassicHttpResponse response1) -> {
                System.out.println("HTTP-CODE: " + response1.getCode());

                String jsonString = EntityUtils.toString(response1.getEntity());

                return jsonString;
                //return new Gson().fromJson(jsonString, OperationResult.class);
            });

            System.out.println("RESPONSE: " + response);

        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

}
