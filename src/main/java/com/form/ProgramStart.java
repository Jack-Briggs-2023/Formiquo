package com.form;

// Imports
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ProgramStart extends Application {
    Rectangle2D screenBounds = Screen.getPrimary().getBounds();
    double screenWidth = screenBounds.getWidth();
    double screenHeight = screenBounds.getHeight();

    Preferences appPreferences = Preferences.userNodeForPackage(ProgramStart.class);

    @Override
    public void start(Stage primaryStage) {
        // Stage (window) setup
        primaryStage.setTitle("Formiquo");
        boolean isFirstLaunch = appPreferences.getBoolean("isFirstLaunch", true);

        StackPane root = new StackPane(); // Root layout container

        // Load custom font from resources
        Font defaultFont = Font.loadFont(
                getClass().getResourceAsStream("/com/form/RobotoFonts/Roboto_Condensed-medium.ttf"),
                14
        );

        // If font loads successfully, override default font styling
        if (defaultFont != null) {
            root.setStyle("-fx-font-family: '" + defaultFont.getFamily() + "'; -fx-font-size: 14px;");
        } else {
            System.out.println("Custom font failed to load.");
        }

        
        Scene scene = new Scene(root, (screenWidth / 5), (screenHeight / 5)); // Scene with root and size
        primaryStage.setScene(scene);

        if (isFirstLaunch) {
            // System.out.println("First launch detected");
            // appPreferences.putBoolean("isFirstLaunch", false);
            // Button resetButton = new Button("First Time");
            // resetButton.setOnAction(event -> {
            //     System.out.println("Button was pressed");
            //     appPreferences.putBoolean("isFirstLaunch", true);
            // });
            // root.getChildren().add(resetButton);
            // ADD CLASS CALL HERE
        } else {
        // Button resetButton = new Button("Reset");
        //     resetButton.setOnAction(event -> {
        //         System.out.println("Button was pressed");
        //         appPreferences.putBoolean("isFirstLaunch", true);
        //     });
        //     root.getChildren().add(resetButton);
            // ADD CLASS CALL HERE
        }


        primaryStage.show(); // Display the stage
    }

    public static void main(String[] args) {
        launch(args); // Launches the JavaFX application
    }
}
