package com.form;

import javafx.application.Application;
import javafx.stage.Stage;

public class ProgramStart extends Application {

    @Override
    public void start(Stage primaryStage) {
        WindowManager.initialize(primaryStage); // Register the stage with WindowManager

        boolean isFirstLaunch = Assets.appPreferences.getBoolean("isFirstLaunch", true); // Check if this is the first launch

        // Route to the correct starting window
        if (isFirstLaunch) {
            WindowManager.switchTo(new WelcomeWindow()); // First launch — show welcome/onboarding
        } else {
            WindowManager.switchTo(new MainMenuWindow()); // Returning user — go straight to main menu
        }

        primaryStage.show(); // Display the stage
    }

    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }

}