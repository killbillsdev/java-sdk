package com.killbills;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Sdk {
    public static List<Map<String, Object>> getStores(String env, String apiKey) {
        return getStores(env, apiKey, 500, 0); 
    }

    public static List<Map<String, Object>> getStores(String env, String apiKey, int limit, int offset) {
        try {
            if (env == null || env.isEmpty()) {
                throw new IllegalArgumentException("No environment specified");
            }
            if (apiKey == null || apiKey.isEmpty()) {
                throw new IllegalArgumentException("No API key provided");
            }

            String baseUrl = "https://w." + (env.equals("prod") ? "" : env + ".") + "killbills."
                    + (env.equals("prod") ? "co" : "dev") + "/stores?limit=" + limit + "&offset=" + offset;
            URL url = new URL(baseUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", apiKey);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                String responseBody = response.toString();

                // Parse the JSON response as an object, not an array
                Map<String, Object> responseObject = parseResponseFromResponseBody(responseBody);

                // Now you can access the "items" array inside the object
                List<Map<String, Object>> stores = parseStoresFromResponseObject(responseObject);
                return stores;
            } else {
                throw new IOException("Request failed with status: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Map<String, Object> parseResponseFromResponseBody(String responseBody) {
        Gson gson = new Gson();
        TypeToken<Map<String, Object>> typeToken = new TypeToken<Map<String, Object>>() {
        };
        return gson.fromJson(responseBody, typeToken.getType());
    }

    private static List<Map<String, Object>> parseStoresFromResponseObject(Map<String, Object> responseObject) {
        // Assuming the "items" field is an array of stores
        return (List<Map<String, Object>>) responseObject.get("items");
    }

    public static String sendReceipt(String env, Map<String, Object> receiptData, String hmacKey) {
        return DataSender.sendDataWithHmac(env, "receipt", receiptData, hmacKey, new ValidatorFunction() {
            @Override
            public boolean validate(Map<String, Object> data) {
                return PayloadValidation.validateReceiptPayload(data);
            }
        });
    }

    public static String sendBankingTransaction(String env, Map<String, Object> receiptData, String hmacKey) {
        return DataSender.sendDataWithHmac(env, "transaction", receiptData, hmacKey, new ValidatorFunction() {
            @Override
            public boolean validate(Map<String, Object> data) {
                return PayloadValidation.validateTransactionPayload(data);
            }
        });
    }

    public static int add(int i, int j) {
        return 0;
    }
}