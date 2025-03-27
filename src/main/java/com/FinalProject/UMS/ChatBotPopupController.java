package com.FinalProject.UMS;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatBotPopupController {
    private static final Logger LOGGER = Logger.getLogger(ChatBotPopupController.class.getName());
    private OpenAIConnector openAIConnector;
    private ObservableList<ChatMessage> messages = FXCollections.observableArrayList();

    @FXML
    private ListView<ChatMessage> chatDisplay;

    @FXML
    private TextArea userInput;

    @FXML
    private Button sendButton;

    @FXML
    private Button closeButton;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    public void initialize() {
        try {
            // Store your API key here (for testing purposes only).
            String apiKey = "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"; // Replace with your API key
            if (apiKey == null || apiKey.trim().isEmpty()) {
                apiKey = System.getenv("OPENAI_API_KEY");
            }
            if (apiKey != null && !apiKey.trim().isEmpty()) {
                openAIConnector = new OpenAIConnector(apiKey);
                messages.add(new ChatMessage("System", "Welcome to UMS Assistant! How can I help you today?", false));
                chatDisplay.setItems(messages);
                chatDisplay.setCellFactory(lv -> new ListCell<ChatMessage>() {
                    private final Text text = new Text();
                    {
                        text.wrappingWidthProperty().bind(chatDisplay.widthProperty().subtract(20));
                    }

                    @Override
                    protected void updateItem(ChatMessage item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            text.setText(item.toString());
                            if (item.isError()) {
                                text.getStyleClass().add("error-message");
                                text.setFill(javafx.scene.paint.Color.RED);
                            } else {
                                text.getStyleClass().remove("error-message");
                                text.setFill(javafx.scene.paint.Color.BLACK);
                            }
                            setGraphic(text);
                        }
                    }
                });

                // Add keyboard shortcut for sending messages
                userInput.setOnKeyPressed(event -> {
                    if (event.isControlDown() && event.getCode().toString().equals("ENTER")) {
                        handleSendMessage();
                    }
                });
            } else {
                messages.add(new ChatMessage("System", "Error: OpenAI API key not found. Please set OPENAI_API_KEY environment variable or openai.api.key system property.", true));
                chatDisplay.setItems(messages);
                userInput.setDisable(true);
                sendButton.setDisable(true);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize chatbot", e);
            messages.add(new ChatMessage("System", "Error initializing chatbot: " + e.getMessage(), true));
            chatDisplay.setItems(messages);
            userInput.setDisable(true);
            sendButton.setDisable(true);
        }
    }

    @FXML
    private void handleSendMessage() {
        if (openAIConnector == null) {
            showError("Connection Error", "Chatbot is not properly initialized.");
            return;
        }

        String message = userInput.getText().trim();
        if (message.isEmpty()) {
            return;
        }

        userInput.setDisable(true);
        sendButton.setDisable(true);
        progressIndicator.setVisible(true);

        messages.add(new ChatMessage("You", message, false));
        userInput.clear();

        new Thread(() -> {
            try {
                String response = openAIConnector.getResponse(message);
                Platform.runLater(() -> {
                    messages.add(new ChatMessage("Assistant", response, false));
                    enableInput();
                    // Scroll to the bottom of the chat
                    chatDisplay.scrollTo(messages.size() - 1);
                });
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error getting response from chatbot", e);
                Platform.runLater(() -> {
                    messages.add(new ChatMessage("System", "Error: " + e.getMessage(), true));
                    enableInput();
                    // Scroll to the bottom of the chat
                    chatDisplay.scrollTo(messages.size() - 1);
                });
            }
        }).start();
    }

    @FXML
    private void handleClose() {
        if (openAIConnector != null) {
            try {
                openAIConnector.close();
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error closing OpenAIConnector", e);
            }
        }
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    private void enableInput() {
        userInput.setDisable(false);
        sendButton.setDisable(false);
        progressIndicator.setVisible(false);
        userInput.requestFocus();
    }

    private void showError(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}