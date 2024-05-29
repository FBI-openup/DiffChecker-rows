package org.projectpiia.View;

import javafx.scene.layout.StackPane;
import org.projectpiia.Model.Model;

import java.io.File;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import org.fxmisc.richtext.StyleClassedTextArea;

/**
 * View of the application
 */
public class View {
    private final Stage primaryStage;
    private final Model model;
    private final TextArea textArea1;
    private final StyleClassedTextArea textArea2;
    private final Button openFile1Button;
    private final Button compareToFile2Button;
    private final Button saveButton;
    private final Button acceptModiButton;
    private final Button rejectModiButton;
    private final Button counter_proposalButton;
    private final Button prevModiButton;
    private final Button nextModiButton;
    private final Button addCommentButton;
    private final Button deleteCommentButton;
    private final Button showCommentsButton;
    private Timeline highlighter;

    public View(Stage primaryStage, Model model) {
        this.primaryStage = primaryStage;
        this.model = model;
        primaryStage.setTitle("DIFF CHECKER");
        primaryStage.setMaximized(true);

        /// STYLES

        String backgroundColor = "-fx-background-color: #D3D3D3;";
        String labelColor = "-fx-background-color: #737373;";
        String layoutBackgroundColor = "-fx-background-color: #535353;";
        String buttonColor = "-fx-background-color: #f0f0f0;";
        String buttonHoverColor = "-fx-background-color: #d0d0d0;";
        String buttonPressedColor = "-fx-background-color: #b0b0b0;";
        String textColor = "-fx-text-fill: #000000;";
        String borderColor = "-fx-border-color: #000000;";
        String borderWidth = "-fx-border-width: 1px;";
        String borderStyle = "-fx-border-style: solid;";
        String borderRadius = "-fx-border-radius: 5px;";
        String padding = "-fx-padding: 5px;";
        String margin = "-fx-margin: 5px;";
        String font = "-fx-font-size: 14px;";
        String labelFont = "-fx-font-size: 16px;";
        String fontColor = "-fx-text-fill: #FFFFFF;";
        String fontFamily = "-fx-font-family: Arial;";
        String fontStyle = "-fx-font-style: normal;";
        String fontWeight = "-fx-font-weight: normal;";
        String textAlignment = "-fx-alignment: center;";
        String textWrapping = "-fx-text-wrapping: true;";
        String textOverflow = "-fx-text-overflow: ellipsis;";
        String cursor = "-fx-cursor: hand;";
        String effect = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 0);";
        String width = "-fx-min-width: 100px;";
        String height = "-fx-min-height: 30px;";
        String spacing = "-fx-spacing: 5px;";
        String textAreaSize = "-fx-min-width: 1300; -fx-min-height: 700px;";

        String buttonStyle = backgroundColor + buttonColor + buttonHoverColor + buttonPressedColor + textColor + borderColor + borderWidth + borderStyle + borderRadius + padding + margin + font + fontFamily + fontStyle + fontWeight + textAlignment + textWrapping + textOverflow + cursor + effect + width + height + spacing;
        String labelStyle = labelColor + textColor + borderColor + borderWidth + borderStyle + borderRadius + padding + margin + labelFont + fontColor + fontFamily + fontStyle + fontWeight + textAlignment + textWrapping + textOverflow + cursor + effect + width + height + spacing;
        String textAreaStyle = backgroundColor + textColor + borderColor + borderWidth + borderStyle + borderRadius + padding + margin + font + fontFamily + fontStyle + fontWeight + textAlignment + textWrapping + textOverflow + cursor + effect + textAreaSize;
        String layoutStyle = layoutBackgroundColor + textColor + borderColor + borderWidth + borderStyle + borderRadius + padding + margin + font + fontFamily + fontStyle + fontWeight + textAlignment + textWrapping + textOverflow + cursor + effect + width + height + spacing;


        /// TEXT AREAS

        // Text Areas
        textArea1 = new TextArea();
        textArea1.setStyle(textAreaStyle);
        textArea1.setEditable(false);
        textArea2 = new StyleClassedTextArea();
        textArea2.getStylesheets().add("file:src/main/resources/styles.css");
        textArea2.setStyle(textAreaStyle);
        textArea2.setEditable(true);

        textArea2.textProperty().addListener((observable, oldValue, newValue) -> {
            long oldCount = oldValue.chars().filter(ch -> ch == '🔄' || ch == '➖' || ch == '➕').count();
            long newCount = newValue.chars().filter(ch -> ch == '🔄' || ch == '➖' || ch == '➕').count();

            if (newCount < oldCount) {
                textArea2.replaceText(oldValue);
            }
        });

        // Scroll Panes
        ScrollPane textArea1ScrollPane = new ScrollPane();
        textArea1ScrollPane.setContent(textArea1);
        textArea1ScrollPane.setFitToWidth(true);
        textArea1ScrollPane.setFitToHeight(true);
        ScrollPane textArea2ScrollPane = new ScrollPane();
        textArea2ScrollPane.setContent(textArea2);
        textArea2ScrollPane.setFitToWidth(true);
        textArea2ScrollPane.setFitToHeight(true);
        // Split Pane
        SplitPane textArea = new SplitPane(textArea1ScrollPane, textArea2ScrollPane);
        textArea.setStyle(textAreaStyle);
        textArea.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        textArea.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);


        /// LABELS

        Label fileLabel = new Label("FILE");
        fileLabel.setStyle(labelStyle);
        Label modificationLabel = new Label("MODIFICATIONS");
        modificationLabel.setStyle(labelStyle);
        Label commentLabel = new Label("COMMENTS");
        commentLabel.setStyle(labelStyle);


        /// BUTTONS

        // Open File 1 Button
        openFile1Button = new Button("Open File 1");
        openFile1Button.setStyle(buttonStyle);
        ImageView openFile1ButtonImage = new ImageView(new Image("file:src/main/resources/open.jpg"));
        openFile1ButtonImage.setFitHeight(20);
        openFile1ButtonImage.setFitWidth(20);
        openFile1Button.setGraphic(openFile1ButtonImage);
        // Compare to File 2 Button
        compareToFile2Button = new Button("Compare to File 2");
        compareToFile2Button.setStyle(buttonStyle);
        ImageView compareToFile2ButtonImage = new ImageView(new Image("file:src/main/resources/2ndfile.jpg"));
        compareToFile2ButtonImage.setFitHeight(20);
        compareToFile2ButtonImage.setFitWidth(20);
        compareToFile2Button.setGraphic(compareToFile2ButtonImage);
        Tooltip compareToFile2ButtonTooltip = new Tooltip("Load the first file first");
        StackPane compareToFile2ButtonContainer = new StackPane();
        compareToFile2ButtonContainer.getChildren().add(compareToFile2Button);
        compareToFile2ButtonContainer.setOnMouseEntered(e -> {
            if (!model.isText1Loaded()) {
                Tooltip.install(compareToFile2ButtonContainer, compareToFile2ButtonTooltip);
            }
        });
        compareToFile2ButtonContainer.setOnMouseExited(e -> {
            compareToFile2ButtonTooltip.hide();
        });
        // Save Button
        saveButton = new Button("Save");
        saveButton.setStyle(buttonStyle);
        ImageView saveButtonImage = new ImageView(new Image("file:src/main/resources/save.jpg"));
        saveButtonImage.setFitHeight(20);
        saveButtonImage.setFitWidth(20);
        saveButton.setGraphic(saveButtonImage);
        Tooltip saveButtonTooltip = new Tooltip("Load the second file first");
        StackPane saveButtonContainer = new StackPane();
        saveButtonContainer.getChildren().add(saveButton);
        saveButtonContainer.setOnMouseEntered(e -> {
            if (!model.isText2Loaded()) {
                Tooltip.install(saveButtonContainer, saveButtonTooltip);
            }
        });

        saveButtonContainer.setOnMouseExited(e -> {
            saveButtonTooltip.hide();
        });
        // Accept Button
        acceptModiButton = new Button("Accept");
        acceptModiButton.setStyle(buttonStyle);
        ImageView acceptModiButtonImage = new ImageView(new Image("file:src/main/resources/accept.jpg"));
        acceptModiButtonImage.setFitHeight(20);
        acceptModiButtonImage.setFitWidth(20);
        acceptModiButton.setGraphic(acceptModiButtonImage);
        Tooltip acceptModiButtonTooltip = new Tooltip("Load the second file first");
        StackPane acceptModiButtonContainer = new StackPane();
        acceptModiButtonContainer.getChildren().add(acceptModiButton);
        acceptModiButtonContainer.setOnMouseEntered(e -> {
            if (!model.isText2Loaded()) {
                Tooltip.install(acceptModiButtonContainer, acceptModiButtonTooltip);
            }
        });
        acceptModiButtonContainer.setOnMouseExited(e -> {
            acceptModiButtonTooltip.hide();
        });
        // Reject Button
        rejectModiButton = new Button("Reject");
        rejectModiButton.setStyle(buttonStyle);
        ImageView rejectModiButtonImage = new ImageView(new Image("file:src/main/resources/reject.jpg"));
        rejectModiButtonImage.setFitHeight(20);
        rejectModiButtonImage.setFitWidth(20);
        rejectModiButton.setGraphic(rejectModiButtonImage);
        Tooltip rejectModiButtonTooltip = new Tooltip("Load the second file first");
        StackPane rejectModiButtonContainer = new StackPane();
        rejectModiButtonContainer.getChildren().add(rejectModiButton);
        rejectModiButtonContainer.setOnMouseEntered(e -> {
            if (!model.isText2Loaded()) {
                Tooltip.install(rejectModiButtonContainer, rejectModiButtonTooltip);
            }
        });
        rejectModiButtonContainer.setOnMouseExited(e -> {
            rejectModiButtonTooltip.hide();
        });
        // Counter Proposal Button
        counter_proposalButton = new Button("Counter Proposal");
        counter_proposalButton.setStyle(buttonStyle);
        ImageView counter_proposalButtonImage = new ImageView(new Image("file:src/main/resources/counter.png"));
        counter_proposalButtonImage.setFitHeight(20);
        counter_proposalButtonImage.setFitWidth(20);
        counter_proposalButton.setGraphic(counter_proposalButtonImage);
        Tooltip counter_proposalButtonTooltip = new Tooltip("Load the second file first");
        StackPane counter_proposalButtonContainer = new StackPane();
        counter_proposalButtonContainer.getChildren().add(counter_proposalButton);
        counter_proposalButtonContainer.setOnMouseEntered(e -> {
            if (!model.isText2Loaded()) {
                Tooltip.install(counter_proposalButtonContainer, counter_proposalButtonTooltip);
            }
        });
        counter_proposalButtonContainer.setOnMouseExited(e -> {
            counter_proposalButtonTooltip.hide();
        });
        // Previous Button
        prevModiButton = new Button("Previous");
        prevModiButton.setStyle(buttonStyle);
        ImageView prevModiButtonImage = new ImageView(new Image("file:src/main/resources/prev.jpg"));
        prevModiButtonImage.setFitHeight(20);
        prevModiButtonImage.setFitWidth(20);
        prevModiButton.setGraphic(prevModiButtonImage);
        Tooltip prevModiButtonTooltip = new Tooltip("Load the second file first");
        StackPane prevModiButtonContainer = new StackPane();
        prevModiButtonContainer.getChildren().add(prevModiButton);
        prevModiButtonContainer.setOnMouseEntered(e -> {
            if (!model.isText2Loaded()) {
                Tooltip.install(prevModiButtonContainer, prevModiButtonTooltip);
            }
        });
        prevModiButtonContainer.setOnMouseExited(e -> {
            prevModiButtonTooltip.hide();
        });
        // Next Button
        nextModiButton = new Button("Next");
        nextModiButton.setStyle(buttonStyle);
        ImageView nextModiButtonImage = new ImageView(new Image("file:src/main/resources/next.jpg"));
        nextModiButtonImage.setFitHeight(20);
        nextModiButtonImage.setFitWidth(20);
        nextModiButton.setGraphic(nextModiButtonImage);
        Tooltip nextModiButtonTooltip = new Tooltip("Load the second file first");
        StackPane nextModiButtonContainer = new StackPane();
        nextModiButtonContainer.getChildren().add(nextModiButton);
        nextModiButtonContainer.setOnMouseEntered(e -> {
            if (!model.isText2Loaded()) {
                Tooltip.install(nextModiButtonContainer, nextModiButtonTooltip);
            }
        });
        nextModiButtonContainer.setOnMouseExited(e -> {
            nextModiButtonTooltip.hide();
        });
        // Add Comment Button
        addCommentButton = new Button("Add Comment");
        addCommentButton.setStyle(buttonStyle);
        ImageView addCommentButtonImage = new ImageView(new Image("file:src/main/resources/add.jpg"));
        addCommentButtonImage.setFitHeight(20);
        addCommentButtonImage.setFitWidth(20);
        addCommentButton.setGraphic(addCommentButtonImage);
        Tooltip addCommentButtonTooltip = new Tooltip("Load the second file first");
        StackPane addCommentButtonContainer = new StackPane();
        addCommentButtonContainer.getChildren().add(addCommentButton);
        addCommentButtonContainer.setOnMouseEntered(e -> {
            if (!model.isText2Loaded()) {
                Tooltip.install(addCommentButtonContainer, addCommentButtonTooltip);
            }
        });
        addCommentButtonContainer.setOnMouseExited(e -> {
            addCommentButtonTooltip.hide();
        });
        // Delete Comment Button
        deleteCommentButton = new Button("Delete Comment");
        deleteCommentButton.setStyle(buttonStyle);
        ImageView deleteCommentButtonImage = new ImageView(new Image("file:src/main/resources/delete.jpg"));
        deleteCommentButtonImage.setFitHeight(20);
        deleteCommentButtonImage.setFitWidth(20);
        deleteCommentButton.setGraphic(deleteCommentButtonImage);
        Tooltip deleteCommentButtonTooltip = new Tooltip("Load the second file first");
        StackPane deleteCommentButtonContainer = new StackPane();
        deleteCommentButtonContainer.getChildren().add(deleteCommentButton);
        deleteCommentButtonContainer.setOnMouseEntered(e -> {
            if (!model.isText2Loaded()) {
                Tooltip.install(deleteCommentButtonContainer, deleteCommentButtonTooltip);
            }
        });
        deleteCommentButtonContainer.setOnMouseExited(e -> {
            deleteCommentButtonTooltip.hide();
        });
        // Show Comments Button
        showCommentsButton = new Button("Show Comments");
        showCommentsButton.setStyle(buttonStyle);
        ImageView showCommentsButtonImage = new ImageView(new Image("file:src/main/resources/show.png"));
        showCommentsButtonImage.setFitHeight(20); // set the size of the image
        showCommentsButtonImage.setFitWidth(20);
        showCommentsButton.setGraphic(showCommentsButtonImage);
        Tooltip showCommentsButtonTooltip = new Tooltip("Load the second file first");
        StackPane showCommentsButtonContainer = new StackPane();
        showCommentsButtonContainer.getChildren().add(showCommentsButton);
        showCommentsButtonContainer.setOnMouseEntered(e -> {
            if (!model.isText2Loaded()) {
                Tooltip.install(showCommentsButtonContainer, showCommentsButtonTooltip);
            }
        });
        showCommentsButtonContainer.setOnMouseExited(e -> {
            showCommentsButtonTooltip.hide();
        });


        /// LAYOUT

        // File layout
        HBox fileButtons = new HBox(openFile1Button, compareToFile2ButtonContainer, saveButtonContainer);
        fileButtons.setSpacing(5);
        VBox fileMenu = new VBox(fileLabel, fileButtons);
        fileMenu.setSpacing(5);
        // Modifications layout
        HBox modiButtons = new HBox(acceptModiButtonContainer, rejectModiButtonContainer, counter_proposalButtonContainer, prevModiButtonContainer, nextModiButtonContainer);
        modiButtons.setSpacing(5);
        VBox modiMenu = new VBox(modificationLabel, modiButtons);
        modiMenu.setSpacing(5);
        // Comments layout
        HBox commentButtons = new HBox(addCommentButtonContainer, deleteCommentButtonContainer, showCommentsButtonContainer);
        commentButtons.setSpacing(5);
        VBox commentMenu = new VBox(commentLabel, commentButtons);
        commentMenu.setSpacing(5);
        // Final layout
        HBox menuBarContent = new HBox(fileMenu, modiMenu, commentMenu);
        menuBarContent.setSpacing(15);
        VBox layout = new VBox(menuBarContent, textArea);
        layout.setStyle(layoutStyle);
        layout.setSpacing(15);
        layout.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

        Scene scene = new Scene(layout, 1450, 750);

        primaryStage.setScene(scene);
    }

    // Update the text in the text area 1 (left text area)
    public void updateTextArea1(String text) {
        textArea1.setText(text);
    }

    // Update the text in the text area 2 (right text area)
    public void updateTextArea2(String text) {
        textArea2.replaceText(text);
    }

    // Highlight text in the text area
    public void highlightText(int start, int end, String style) {
        textArea2.setStyleClass(start, end, style);
    }

    // Start the highlight animation (highlight/unhighlight)
    public void startHighlightAnimation(int start, int end, String state) {
        if (highlighter != null) {
            highlighter.stop();
        }
        textArea2.setStyleClass(start, end, "highlight");
        var ref = new Object() {
            boolean isHighlighted = false;
        };
        highlighter = new Timeline(new KeyFrame(
                Duration.millis(1000),
                ae -> {
                    if (ref.isHighlighted) {
                        textArea2.setStyleClass(start, end, state);
                        ref.isHighlighted = false;
                    } else {
                        textArea2.setStyleClass(start, end, "highlight");
                        ref.isHighlighted = true;
                    }
                }
        ));

        highlighter.setCycleCount(Timeline.INDEFINITE);
        highlighter.play();
    }

    // Stop the highlight animation
    public void stopHighlightAnimation() {
        if (highlighter != null) {
            highlighter.stop();
        }
    }

    // Getters

    public StyleClassedTextArea getTextArea2() {
        return textArea2;
    }

    public Button getOpenFile1Button() {
        return openFile1Button;
    }

    public Button getCompareToFile2Button() {
        return compareToFile2Button;
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public Button getNextModiButton() {
        return nextModiButton;
    }

    public Button getPrevModiButton() {
        return prevModiButton;
    }

    public Button getAcceptModiButton() {
        return acceptModiButton;
    }

    public Button getRejectModiButton() {
        return rejectModiButton;
    }

    public Button getCounterProposalButton() {
        return counter_proposalButton;
    }

    public Button getAddCommentButton() {
        return addCommentButton;
    }

    public Button getDeleteCommentButton() {
        return deleteCommentButton;
    }

    public Button getShowCommentsButton() {
        return showCommentsButton;
    }

    // Get the file path of the file to open
    public String getFilePath() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            return file.getAbsolutePath();
        } else {
            return "";
        }
    }

    // Get the file path of the file to save
    public String getSaveFilePath() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            return file.getAbsolutePath();
        } else {
            return "";
        }
    }
}