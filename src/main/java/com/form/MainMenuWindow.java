package com.form;

import java.io.File;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class MainMenuWindow implements StartWindow {

    @Override
    public Scene buildScene() {
        BorderPane root = new BorderPane();
        Assets.mainWindowRoot = root; // Update shared root reference
        Assets.setFont(root, "/com/form/RobotoFonts/Roboto_Condensed-medium.ttf", 14);

        // --- Top: app title ---
        Text appTitle = new Text("Formiquo");
        root.setTop(appTitle);
        BorderPane.setAlignment(appTitle, Pos.CENTER);
        BorderPane.setMargin(appTitle, new Insets(24, 0, 0, 0)); // Top padding

        // --- Center: main action buttons ---
        VBox menuButtons = new VBox(14); // 14px gap between buttons
        menuButtons.setAlignment(Pos.CENTER);

        // New resume — opens a blank resume form
        Button newResumeButton = new Button("New Resume");
        newResumeButton.setMaxWidth(200);
        newResumeButton.setOnAction(event -> {
            WindowManager.switchTo(new ResumeFormWindow(null)); // null = no existing data, start blank
        });

        // Load resume — opens a file picker for a saved JSON resume file
        Button loadResumeButton = new Button("Load Resume");
        loadResumeButton.setMaxWidth(200);
        loadResumeButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resume File");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Resume Files (*.json)", "*.json") // Only show .json files
            );
            File selectedFile = fileChooser.showOpenDialog(WindowManager.getStage());
            if (selectedFile != null) {
                WindowManager.switchTo(new ResumeFormWindow(selectedFile)); // Pass file to form for loading
            }
        });

        // Settings — opens the settings window
        Button settingsButton = new Button("Settings");
        settingsButton.setMaxWidth(200);
        settingsButton.setOnAction(event -> {
            WindowManager.switchTo(new SettingsWindow());
        });

        menuButtons.getChildren().addAll(newResumeButton, loadResumeButton, settingsButton);
        root.setCenter(menuButtons);

        // --- Bottom: version label ---
        Text versionLabel = new Text("v0.1");
        root.setBottom(versionLabel);
        BorderPane.setAlignment(versionLabel, Pos.CENTER);
        BorderPane.setMargin(versionLabel, new Insets(0, 0, 12, 0)); // Bottom padding

        return new Scene(root, Assets.windowWidth, Assets.windowHeight);
    }

    @Override
    public void onShow() {
        WindowManager.getStage().setTitle("Formiquo - Main Menu");
        // Apply saved theme — returning users skip WelcomeWindow so theme must be applied here
        boolean isLightMode = Assets.appPreferences.getBoolean("isLightMode", true);
        Assets.setLightOrDarkMode(isLightMode);
    }

}