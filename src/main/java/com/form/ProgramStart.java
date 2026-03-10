package com.form;

// Imports
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ProgramStart extends Application {
    Rectangle2D screenBounds = Screen.getPrimary().getBounds();
    double screenWidth = screenBounds.getWidth();
    double screenHeight = screenBounds.getHeight();



    @Override
    public void start(Stage primaryStage) {
        // Stage (window) setup
        primaryStage.setTitle("Formiquo");
        boolean isFirstLaunch = Assets.appPreferences.getBoolean("isFirstLaunch", true); // get the preference isFirstLaunch

        BorderPane primarRoot = new BorderPane(); // Root layout container
        Assets.mainWindowRoot = primarRoot; // adds the current root to mainWindowRoot for future reference
        Assets.setFont(Assets.mainWindowRoot, "/com/form/RobotoFonts/Roboto_Condensed-medium.ttf", 14);

        Scene scene = new Scene(Assets.mainWindowRoot, (screenWidth / 3), (screenHeight / 3)); // Create scene with root and size
        primaryStage.setScene(scene);


        if (isFirstLaunch) {
            Assets.appPreferences.putBoolean("isFirstLaunch", false);
            Button resetButton = new Button("First Time");
            resetButton.setOnAction(event -> {
                Assets.appPreferences.putBoolean("isFirstLaunch", true);
            });
            Assets.mainWindowRoot.setCenter(resetButton);
            
            // Adds objecets to te welcome window
            Text welcomeTextTitle = new Text("Welcome to Formiquo\n System Settings");
            Assets.mainWindowRoot.setCenter(welcomeTextTitle);
            Button continueToMainPageButton = new Button("Continue to Main Menu");
            continueToMainPageButton.setOnAction(event -> {
            });
        Assets.mainWindowRoot.setBottom(continueToMainPageButton);
        } else {
            Button resetButton = new Button("Reset");
            resetButton.setOnAction(event -> {
                Assets.appPreferences.putBoolean("isFirstLaunch", true);
            });
            Assets.mainWindowRoot.setCenter(resetButton);
            // ADD CLASS CALL HERE
        }


        primaryStage.show(); // Display the stage
    }

    public static void main(String[] args) {
        launch(args); // Launches the JavaFX application
    }
}
