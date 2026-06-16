package com.form;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class WindowManager {

    static Stage primaryStage;  // The single app window shared across all scenes
    static Scene currentScene;  // The active scene — used by Assets.setLightOrDarkMode()

    // Called once at startup in ProgramStart to register the stage
    public static void initialize(Stage stage) {
        primaryStage = stage;
    }

    // Switches the app to a new window/scene
    // Builds the scene, stores it, swaps it on the stage, then calls onShow()
    public static void switchTo(StartWindow window) {
        Scene newScene = window.buildScene();

        currentScene = newScene;          // Store before onShow so setLightOrDarkMode can reach it
        primaryStage.setScene(newScene);

        // primaryStage.setWidth(currentScene.getWidth());
        // primaryStage.setHeight(currentScene.getHeight());
        boolean isLightMode = Assets.appPreferences.getBoolean("isLightMode", true);
        Assets.setLightOrDarkMode(isLightMode);
        window.onShow();
    }

    public static Stage getStage() {
        return primaryStage;
    }

    public static Scene getCurrentScene() {
        return currentScene;
    }

}