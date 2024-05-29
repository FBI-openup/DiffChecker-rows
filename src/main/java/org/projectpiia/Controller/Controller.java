package org.projectpiia.Controller;

import org.projectpiia.Model.Model;
import org.projectpiia.Model.Modification;
import org.projectpiia.View.View;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;


/**
 * Controller class of the application
 */
public class Controller {
    private final Model model;
    private final View view;
    private List<Modification> modifications = new ArrayList<>();
    private int currentModificationIndex;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
        this.currentModificationIndex = 0;

        view.getOpenFile1Button().setOnAction(this::handleOpenFile1ButtonAction);
        view.getCompareToFile2Button().setOnAction(this::handleCompareToFile2ButtonAction);
        view.getCompareToFile2Button().setDisable(true);
        view.getSaveButton().setOnAction(this::handleSaveButtonAction);
        view.getSaveButton().setDisable(true);
        view.getNextModiButton().setOnAction(this::handleNextButtonAction);
        view.getPrevModiButton().setOnAction(this::handlePrevButtonAction);
        view.getAcceptModiButton().setOnAction(this::handleAcceptButtonAction);
        view.getRejectModiButton().setOnAction(this::handleRejectButtonAction);
        view.getCounterProposalButton().setOnAction(this::handleCounterProposalButtonAction);
        view.getAddCommentButton().setOnAction(this::handleAddCommentButtonAction);
        view.getDeleteCommentButton().setOnAction(this::handleDeleteCommentButtonAction);
        view.getShowCommentsButton().setOnAction(this::handleShowCommentsButtonAction);

        // Thread that updates the clickable state of the buttons depending on the actions performed
        new Thread(() -> {
            while (true) {
                Platform.runLater(() -> {
                    view.getNextModiButton().setDisable(currentModificationIndex >= modifications.size() - 1);
                    view.getPrevModiButton().setDisable(currentModificationIndex <= 0);
                    view.getAcceptModiButton().setDisable(modifications.isEmpty());
                    view.getRejectModiButton().setDisable(modifications.isEmpty());
                    view.getAddCommentButton().setDisable(modifications.isEmpty());
                    view.getDeleteCommentButton().setDisable(modifications.isEmpty() || modifications.get(currentModificationIndex).getComment().isEmpty());
                    view.getShowCommentsButton().setDisable(modifications.isEmpty());
                    view.getCounterProposalButton().setDisable(modifications.isEmpty());
                });

                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // Thread that highlights the modifications that have been treated already and that are not the current one
        new Thread(() -> {
            while (true) {
                Platform.runLater(() -> {
                    for (int i = 0; i < modifications.size(); i++) {
                        if (i != currentModificationIndex) {
                            Modification modification = modifications.get(i);
                            highlightModificationTreated(modification);
                        }
                    }
                });

                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    /// HANDLE BUTTON ACTIONS ///

    // Handle the action when the user clicks on the "Open File 1" button (to open the first file)
    public void handleOpenFile1ButtonAction(ActionEvent event) {
        String path = view.getFilePath();

        try {
            if (Files.exists(Paths.get(path))) {
                model.setPath1(path);
                if (path != null) {
                    model.loadFile1();
                    view.getCompareToFile2Button().setDisable(false);
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("The file does not exist");
                alert.showAndWait();
                return;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        view.updateTextArea1(model.getText1());
    }

    // Handle the action when the user clicks on the "Compare to File 2" button (to open the second file and compare it to the first one)
    public void handleCompareToFile2ButtonAction(ActionEvent event) {
        String path = view.getFilePath();
        try {
            if (Files.exists(Paths.get(path))) {
                model.setPath2(path);
                model.loadFile2();
                String diffText = model.diffText();
                view.updateTextArea2(diffText);
                modifications = model.generateModifications();
                Modification currentModification = modifications.get(currentModificationIndex);
                highlightModification(currentModification);
                view.getSaveButton().setDisable(false);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("The file does not exist");
                alert.showAndWait();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    // Handle the action when the user clicks on the "Save" button (to save the text with the modifications depending on the user's choices)
    public void handleSaveButtonAction(ActionEvent event) {
        try {
            if (model.isText2Loaded()) {
                if (modifications.stream().anyMatch(mod -> mod.getState() == Modification.State.WAITING)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("All modifications must be treated before saving");
                    alert.showAndWait();
                    return;
                }

                String path = view.getSaveFilePath();

                StringBuilder finalText = new StringBuilder();

                List<String> lines = Arrays.asList(model.getText1().split("\\R"));

                for (int i = 0; i < lines.size(); i++) {
                    int finalI = i;
                    Optional<Modification> optionalMod = modifications.stream().filter(mod -> mod.getPosition() == finalI).findFirst();
                    if (optionalMod.isPresent()) {
                        Modification mod = optionalMod.get();
                        if (mod.getState() == Modification.State.ACCEPTED || mod.getState() == Modification.State.COUNTER_PROPOSAL) {
                            finalText.append(mod.getModifiedText()).append("\n");
                        } else {
                            finalText.append(lines.get(i)).append("\n");
                        }
                    } else {
                        finalText.append(lines.get(i)).append("\n");
                    }
                }
                Files.write(Paths.get(path), finalText.toString().getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Handle the action when the user clicks on the "Next Modification" button (to go to the next modification)
    public void handleNextButtonAction(ActionEvent event) {
        if (currentModificationIndex < modifications.size() - 1) {
            if (currentModificationIndex >= 0) {
                Modification currentModification = modifications.get(currentModificationIndex);
                clearModificationStyle(currentModification);
                view.stopHighlightAnimation(); //
            }

            // Highlight the next modification
            currentModificationIndex++;
            Modification nextModification = modifications.get(currentModificationIndex);
            highlightModification(nextModification);
        }

        view.getNextModiButton().setDisable(currentModificationIndex >= modifications.size() - 1);
    }

    // Handle the action when the user clicks on the "Previous Modification" button (to go back to the previous modification)
    public void handlePrevButtonAction(ActionEvent event) {
        if (currentModificationIndex > 0) {
            Modification currentModification = modifications.get(currentModificationIndex);
            clearModificationStyle(currentModification);
            view.stopHighlightAnimation(); //

            currentModificationIndex--;
            Modification prevModification = modifications.get(currentModificationIndex);
            highlightModification(prevModification);
        }

        view.getPrevModiButton().setDisable(currentModificationIndex <= 0);
    }

    // Handle the action when the user clicks on the "Accept Modification" button (to accept the current modification)
    public void handleAcceptButtonAction(ActionEvent event) {
        view.stopHighlightAnimation();
        Modification currentModification = modifications.get(currentModificationIndex);
        currentModification.setState(Modification.State.ACCEPTED);
        highlightModification(currentModification);
    }

    // Handle the action when the user clicks on the "Reject Modification" button (to reject the current modification)
    public void handleRejectButtonAction(ActionEvent event) {
        view.stopHighlightAnimation();
        Modification currentModification = modifications.get(currentModificationIndex);
        currentModification.setState(Modification.State.REJECTED);
        highlightModification(currentModification);
    }

    // Handle the action when the user clicks on the "Counter Proposal" button (to make a counter proposal for the current modification)
    public void handleCounterProposalButtonAction(ActionEvent event) {
        Modification currentModification = modifications.get(currentModificationIndex);
        if (currentModification != null) {
            view.stopHighlightAnimation(); //
            String newContent = view.getTextArea2().getText(currentModification.getPosition());
            currentModification.setModifiedText(newContent);
            currentModification.setState(Modification.State.COUNTER_PROPOSAL);
            highlightModification(currentModification);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No modification selected to make a counter proposal");
            alert.showAndWait();
        }
    }


    // Handle the action when the user clicks on the "Add Comment" button (to add a comment to the current modification)
    public void handleAddCommentButtonAction(ActionEvent event) {
        Modification currentModification = modifications.get(currentModificationIndex);
        if (currentModification != null) {
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Add Comment");
            dialog.setHeaderText("Type your comment here:");

            ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(submitButtonType);

            Label label = new Label("Comment:");
            TextArea textArea = new TextArea(currentModification.getComment());
            textArea.setPromptText("Type your comment here...");

            VBox vbox = new VBox();
            vbox.getChildren().addAll(label, textArea);
            dialog.getDialogPane().setContent(vbox);

            Platform.runLater(() -> textArea.requestFocus());

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == submitButtonType) {
                    return textArea.getText();
                }
                return null;
            });

            dialog.getDialogPane().setPrefSize(300, 200);

            dialog.showAndWait();

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            dialog.setX(screenBounds.getMaxX() - dialog.getWidth());
            dialog.setY(screenBounds.getMinY());

            Optional<String> result = dialog.getResult().describeConstable();
            if (result.isPresent()) {
                currentModification.setComment(result.get());
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No modification selected to add a comment");
            alert.showAndWait();
        }
    }

    // Handle the action when the user clicks on the "Delete Comment" button (to delete the comment of the current modification)
    public void handleDeleteCommentButtonAction(ActionEvent event) {
        Modification currentModification = modifications.get(currentModificationIndex);
        if (currentModification != null) {
            // Create a confirmation dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Are you sure you want to delete the comment?");

            // Show the dialog and wait for the user's response
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // If the user confirms, delete the comment
                currentModification.setComment("");
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No comment to delete");
            alert.showAndWait();
        }
    }

    // Handle the action when the user clicks on the "Show Comment" button (to show the comment of the current modification)
    public void handleShowCommentsButtonAction(ActionEvent event) {
        Modification currentModification = modifications.get(currentModificationIndex);

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Show Comment");

        VBox vbox = new VBox();
        vbox.setSpacing(5);

        String comment = currentModification.getComment();
        if (comment != null && !comment.isEmpty()) {
            TextArea commentArea = new TextArea(comment);
            commentArea.setEditable(false); // Make the TextArea uneditable
            vbox.getChildren().add(commentArea);
        } else {
            vbox.getChildren().add(new Label("No comment available."));
        }

        ScrollPane scrollPane = new ScrollPane(vbox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Scene dialogScene = new Scene(scrollPane, 300, 200);
        dialog.setScene(dialogScene);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        dialog.setX(screenBounds.getMaxX() - dialogScene.getWidth());
        dialog.setY(screenBounds.getMinY());

        dialog.show();
    }


    /// HELPER METHODS ///

    // Highlight the current modification with a blinking effect
    private void highlightModification(Modification modification) {
        int lineNumber = modification.getPosition();
        int start = view.getTextArea2().getAbsolutePosition(lineNumber, 0);
        int end = view.getTextArea2().getAbsolutePosition(lineNumber + 1, 0);

        if (modification.getState() == Modification.State.ACCEPTED) {
            view.startHighlightAnimation(start, end, "accepted");
        } else if (modification.getState() == Modification.State.REJECTED) {
            view.startHighlightAnimation(start, end, "rejected");
        } else if (modification.getState() == Modification.State.COUNTER_PROPOSAL) {
            view.startHighlightAnimation(start, end, "counter_proposal");
        } else {
            view.startHighlightAnimation(start, end, "");
        }
    }
    /*
    private void highlightModification(Modification modification) {
        String textToHighlight = getTextToHighlight(modification);
        int start = view.getTextArea2().getText().indexOf(textToHighlight);
        int end = start + textToHighlight.length();
        if (modification.getState() == Modification.State.ACCEPTED) {
            view.startHighlightAnimation(start, end, "accepted" ); //
        } else if (modification.getState() == Modification.State.REJECTED) {
            view.startHighlightAnimation(start, end, "rejected" ); //
        } else {
            view.startHighlightAnimation(start, end, "");
        }
    }*/

    // Highlight a modification that has already been treated (for the modifications that are not the current one)
    // Highlight a modification that has already been treated (for the modifications that are not the current one)
    private void highlightModificationTreated(Modification modification) {
        int lineNumber = modification.getPosition();
        int start = view.getTextArea2().getAbsolutePosition(lineNumber, 0);
        int end = view.getTextArea2().getAbsolutePosition(lineNumber + 1, 0);

        if (modification.getState() == Modification.State.ACCEPTED) {
            view.highlightText(start, end, "accepted");
        } else if (modification.getState() == Modification.State.REJECTED) {
            view.highlightText(start, end, "rejected");
        } else if (modification.getState() == Modification.State.COUNTER_PROPOSAL) {
            view.highlightText(start, end, "counter_proposal");
        }
    }
    /*
    private void highlightModificationTreated(Modification modification) {
        String textToHighlight = getTextToHighlight(modification);
        int start = view.getTextArea2().getText().indexOf(textToHighlight);
        int end = start + textToHighlight.length();
        if (modification.getState() == Modification.State.ACCEPTED) {
            view.highlightText(start, end, "accepted");
        } else if (modification.getState() == Modification.State.REJECTED) {
            view.highlightText(start, end, "rejected");
        }
    }*/

    // Clear the style of a modification
    // Clear the style of a modification
    private void clearModificationStyle(Modification modification) {
        int lineNumber = modification.getPosition();
        int start = view.getTextArea2().getAbsolutePosition(lineNumber, 0);
        int end = view.getTextArea2().getAbsolutePosition(lineNumber + 1, 0);
        view.getTextArea2().clearStyle(start, end);
    }
    /*
    private void clearModificationStyle(Modification modification) {
        String textToClear = getTextToHighlight(modification);
        int start = view.getTextArea2().getText().indexOf(textToClear);
        int end = start + textToClear.length();
        view.getTextArea2().clearStyle(start, end);
    }*/

    // Get the text to highlight for a modification
    private String getTextToHighlight(Modification modification) {
        switch (modification.getType()) {
            case REPLACE:
                return "🔄" + modification.getModifiedText();
            case DELETE:
                return "➖" + modification.getOriginalText();
            case INSERT:
                return "➕" + modification.getModifiedText();
            default:
                return modification.getOriginalText();
        }
    }

}