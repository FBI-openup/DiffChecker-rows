package org.projectpiia.Application;

import org.projectpiia.Controller.Controller;
import org.projectpiia.Model.Model;
import org.projectpiia.View.View;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main class of the application
 */
public class DiffChecker extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Model model = new Model();
        View view = new View(primaryStage, model);
        Controller controller = new Controller(model, view);

        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            primaryStage.close();
        });
    }

    // Launch the application
    public static void main(String[] args) {
        Application.launch(args);
    }
}