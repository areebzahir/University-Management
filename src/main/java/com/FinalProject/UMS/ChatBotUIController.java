package com.FinalProject.UMS;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;

public class ChatBotUIController {

    @FXML
    private ListView<String> chatDisplay;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private ObservableList<String> messages = FXCollections.observableArrayList();

    // Store your API key here (for testing purposes only).
    private static final String API_KEY = "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"; // Replace with your API key

    // Instantiate the OpenAIConnector with your API key.
    private OpenAIConnector openAIConnector = new OpenAIConnector(API_KEY);

    public void initialize() {
        chatDisplay.setItems(messages);
    }

    @FXML
    private void sendMessage() {
        String message = userInput.getText();
        messages.add("You: " + message);

        try {
            String response = openAIConnector.getResponse(message);
            messages.add("Chatbot: " + response);
        } catch (IOException e) {
            messages.add("Chatbot: Error: " + e.getMessage());
        }

        userInput.clear();
    }
}