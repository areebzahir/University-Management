package com.FinalProject.UMS;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

    /**
     * Connector class for interacting with the OpenAI API.
     * Handles authentication, request formatting, and response parsing.
     */
    public class OpenAIConnector implements AutoCloseable {
        private static final String API_URL = "https://api.openai.com/v1/chat/completions";
        private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        private static final int MAX_RETRIES = 3;
        private static final long RETRY_DELAY_MS = 1000;

        private final String apiKey;
        private final OkHttpClient client;
        private final Gson gson;

        /**
         * Constructs a new OpenAIConnector with the specified API key.
         *
         * @param apiKey The OpenAI API key
         * @throws IllegalArgumentException if the API key is null or empty
         */
        public OpenAIConnector(String apiKey) {
            if (apiKey == null || apiKey.trim().isEmpty()) {
                throw new IllegalArgumentException("API key cannot be null or empty");
            }
            if (!apiKey.startsWith("sk-")) {
                throw new IllegalArgumentException("Invalid API key format. OpenAI API keys should start with 'sk-'");
            }

            this.apiKey = apiKey.trim();
            this.gson = new Gson();

            this.client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .build();
        }

        /**
         * Sends a message to the OpenAI API and returns the response.
         *
         * @param message The message to send to the API
         * @return The response from the API
         * @throws IOException if there's an error communicating with the API
         */
        public String getResponse(String message) throws IOException {
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", "gpt-3.5-turbo");
            
            JsonObject messageObj = new JsonObject();
            messageObj.addProperty("role", "user");
            messageObj.addProperty("content", message);
            
            JsonObject[] messages = new JsonObject[]{messageObj};
            requestBody.add("messages", gson.toJsonTree(messages));
            requestBody.addProperty("temperature", 0.7);
            requestBody.addProperty("max_tokens", 1000);

            RequestBody body = RequestBody.create(requestBody.toString(), JSON);

            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            int retryCount = 0;
            IOException lastException = null;

            while (retryCount < MAX_RETRIES) {
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        String errorBody = response.body() != null ? response.body().string() : "No error body";
                        throw new IOException("API request failed with code " + response.code() + ": " + errorBody);
                    }

                    String responseBody = response.body().string();
                    JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
                    
                    if (!jsonResponse.has("choices") || jsonResponse.getAsJsonArray("choices").size() == 0) {
                        throw new IOException("Invalid response format from API");
                    }

                    return jsonResponse.getAsJsonArray("choices")
                            .get(0).getAsJsonObject()
                            .getAsJsonObject("message")
                            .get("content").getAsString();
                } catch (IOException e) {
                    lastException = e;
                    retryCount++;
                    if (retryCount < MAX_RETRIES) {
                        try {
                            Thread.sleep(RETRY_DELAY_MS * retryCount);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new IOException("Request interrupted", ie);
                        }
                    }
                }
            }

            throw new IOException("Failed after " + MAX_RETRIES + " attempts. Last error: " + lastException.getMessage());
        }

        /**
         * Closes the OpenAIConnector and releases its resources.
         */
        @Override
        public void close() {
            if (client != null) {
                client.dispatcher().executorService().shutdown();
            }
        }
    }