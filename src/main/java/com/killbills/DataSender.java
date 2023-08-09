package com.killbills;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

interface ValidatorFunction {
    boolean validate(Map<String, Object> data);
}

public class DataSender {

    public static String sendDataWithHmac(String env, String endpoint, Map<String, Object> data, String hmacSignature,
            ValidatorFunction validator) {
        try {
            if (data == null || hmacSignature == null || hmacSignature.isEmpty()) {
                throw new IllegalArgumentException("You have not provided Data or Hmac Signature");
            }

            boolean isPayloadValid = validator.validate(data);
            if (!isPayloadValid) {
                throw new IllegalArgumentException("Payload validation failed");
            }

            String hashedPayload;
            try {
                hashedPayload = CryptoUtils.cipherHmacPayload(new Gson().toJson(data), hmacSignature);
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to cipher HMAC payload", e);
            }
            String authorizationHeader = "hmac " + hashedPayload;

            String urlString = "https://in." + (env.equals("prod") ? "" : env + ".") + "killbills."
                    + (env.equals("prod") ? "co" : "dev") + "/" + endpoint;

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", authorizationHeader);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Write JSON data to the request body
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = new Gson().toJson(data).getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            String responseBody = null;
            try (InputStream inputStream = connection.getInputStream()) {
                responseBody = readInputStream(inputStream);
            } catch (IOException e) {
                // If the response code is 400 or higher, read the error response
                if (responseCode >= 400) {
                    responseBody = readInputStream(connection.getErrorStream());
                }
            }

            connection.disconnect();
            return responseBody;

        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }

    }

    private static String readInputStream(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }
    // ... Define ValidatorFunction interface and its implementation here

    public static void main(String[] args) {
        // Call sendDataWithHmac and handle the result
    }
}
