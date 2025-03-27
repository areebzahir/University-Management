package com.FinalProject.UMS;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatBotUI extends Application {
    private static final Logger LOGGER = Logger.getLogger(ChatBotUI.class.getName());
    private static final String API_KEY = "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"; // Store API key here
    private static final String STYLE_CSS = """
            .root {
                -fx-background-color: #f5f5f5;
            }
            .chat-display {
                -fx-background-color: white;
                -fx-border-color: #e0e0e0;
                -fx-border-radius: 5;
            }
            .input-area {
                -fx-background-color: white;
                -fx-border-color: #e0e0e0;
                -fx-border-radius: 5;
                -fx-padding: 10;
            }
            .send-button {
                -fx-background-color: #2196F3;
                -fx-text-fill: white;
                -fx-border-radius: 5;
                -fx-padding: 8 15;
            }
            .send-button:hover {
                -fx-background-color: #1976D2;
            }
            .send-button:disabled {
                -fx-background-color: #BDBDBD;
            }
            .error-message {
                -fx-text-fill: #D32F2F;
            }
            """;

    private OpenAIConnector openAIConnector;
    private ObservableList<ChatMessage> messages = FXCollections.observableArrayList();
    private ListView<ChatMessage> chatDisplay;
    private TextArea userInput;
    private Button sendButton;
    private ProgressIndicator progressIndicator;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        try {
            this.primaryStage = primaryStage;
            initializeChatBot(); // Initialize ChatBot without API Key parameters
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to start ChatBotUI", e);
            showError("Startup Error", "Failed to start the application: " + e.getMessage());
        }
    }

    private void initializeChatBot() { // Initialize ChatBot with API Key
        try {
            openAIConnector = new OpenAIConnector(API_KEY); // Use API_KEY directly
            setupUI();
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, "Invalid API key format", e);
            showError("Invalid API Key", "The provided API key is not in the correct format. Please check and try again.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize OpenAIConnector", e);
            showError("Initialization Error", "Failed to initialize chatbot: " + e.getMessage());
        }
    }

    private void setupUI() {
        try {
            primaryStage.setTitle("University Management System Chatbot");
            primaryStage.setMinWidth(600);
            primaryStage.setMinHeight(400);

            // Chat display
            chatDisplay = new ListView<>(messages);
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
                        } else {
                            text.getStyleClass().remove("error-message");
                        }
                        setGraphic(text);
                    }
                }
            });
            chatDisplay.setEditable(false);
            chatDisplay.getStyleClass().add("chat-display");
            VBox.setVgrow(chatDisplay, Priority.ALWAYS);

            // Input area
            HBox inputArea = new HBox(10);
            inputArea.getStyleClass().add("input-area");
            inputArea.setAlignment(Pos.CENTER);

            userInput = new TextArea();
            userInput.setPrefRowCount(3);
            userInput.setWrapText(true);
            userInput.setPromptText("Type your message here...");
            userInput.setOnKeyPressed(event -> {
                if (event.isControlDown() && event.getCode().toString().equals("ENTER")) {
                    sendMessage();
                }
            });
            HBox.setHgrow(userInput, Priority.ALWAYS);

            sendButton = new Button("Send");
            sendButton.getStyleClass().add("send-button");
            sendButton.setOnAction(event -> sendMessage());
            sendButton.setMinWidth(80);

            progressIndicator = new ProgressIndicator();
            progressIndicator.setVisible(false);
            progressIndicator.setMinWidth(20);
            progressIndicator.setMinHeight(20);

            inputArea.getChildren().addAll(userInput, sendButton, progressIndicator);

            // Main layout
            VBox mainLayout = new VBox(10);
            mainLayout.setPadding(new Insets(10));
            mainLayout.getChildren().addAll(chatDisplay, inputArea);

            // Apply styles
            Scene scene = new Scene(mainLayout);
            scene.getStylesheets().add("data:text/css," + STYLE_CSS);

            primaryStage.setScene(scene);
            primaryStage.show();

            // Handle window closing
            primaryStage.setOnCloseRequest(event -> {
                if (openAIConnector != null) {
                    try {
                        openAIConnector.close();
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Error closing OpenAIConnector", e);
                    }
                }
            });

            // Add welcome message
            messages.add(new ChatMessage("System", "Welcome to the University Management System Chatbot! How can I help you today?", false));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to setup UI", e);
            showError("UI Setup Error", "Failed to setup the user interface: " + e.getMessage());
        }
    }

    private void sendMessage() {
        if (openAIConnector == null) {
            showError("Connection Error", "Chatbot is not properly initialized. Please restart the application.");
            return;
        }

        String message = userInput.getText().trim();
        if (message.isEmpty()) {
            return;
        }

        // Disable input while processing
        userInput.setDisable(true);
        sendButton.setDisable(true);
        progressIndicator.setVisible(true);

        messages.add(new ChatMessage("You", message, false));
        userInput.clear();

        // Use a separate thread for the API call
        new Thread(() -> {
            try {
                String response = openAIConnector.getResponse(message);
                Platform.runLater(() -> {
                    messages.add(new ChatMessage("Chatbot", response, false));
                    enableInput();
                });
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error getting response from chatbot", e);
                Platform.runLater(() -> {
                    messages.add(new ChatMessage("System", "Error: " + e.getMessage(), true));
                    enableInput();
                });
            }
        }).start();
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
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

}