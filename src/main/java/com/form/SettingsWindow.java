package com.form;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class SettingsWindow implements StartWindow {

    @Override
    public Scene buildScene() {
        BorderPane root = new BorderPane();
        Assets.mainWindowRoot = root; // Update shared root reference
        Assets.setFont(root, "/com/form/RobotoFonts/Roboto_Condensed-medium.ttf", 14);

        // --- Top: settings title ---
        Text settingsTitle = new Text("Settings");
        root.setTop(settingsTitle);
        BorderPane.setAlignment(settingsTitle, Pos.CENTER);
        BorderPane.setMargin(settingsTitle, new Insets(24, 0, 0, 0)); // Top padding

        // --- Center: settings options ---
        VBox settingsContent = new VBox(16); // 16px gap between setting rows
        settingsContent.setAlignment(Pos.CENTER_LEFT);
        settingsContent.setPadding(new Insets(20, 40, 20, 40)); // Left/right padding keeps content inset from edges

        // -- Theme setting --
        Text themeLabel = new Text("Theme:");

        ToggleGroup themeToggleGroup = new ToggleGroup();

        RadioButton lightModeButton = new RadioButton("Light Mode");
        lightModeButton.setToggleGroup(themeToggleGroup);

        RadioButton darkModeButton = new RadioButton("Dark Mode");
        darkModeButton.setToggleGroup(themeToggleGroup);

        // Pre-select whichever theme is currently saved
        boolean savedIsLightMode = Assets.appPreferences.getBoolean("isLightMode", true);
        lightModeButton.setSelected(savedIsLightMode);
        darkModeButton.setSelected(!savedIsLightMode);

        HBox themeOptions = new HBox(20, lightModeButton, darkModeButton); // 20px gap between buttons

        // -- Separator between setting groups --
        Separator separator = new Separator();

        // -- Reset setting --
        Text resetLabel = new Text("Reset App:");

        Button resetAppButton = new Button("Reset to First Launch");
        resetAppButton.setOnAction(event -> {
            Assets.appPreferences.putBoolean("isFirstLaunch", true); // Flags app to show WelcomeWindow on next launch
            Assets.appPreferences.remove("isLightMode"); // Clears saved theme preference
        });

        settingsContent.getChildren().addAll(
            themeLabel, themeOptions,
            separator,
            resetLabel, resetAppButton
        );
        root.setCenter(settingsContent);

        // --- Bottom: back and save buttons ---
        HBox bottomButtons = new HBox(12); // 12px gap between buttons
        bottomButtons.setAlignment(Pos.CENTER_RIGHT);
        bottomButtons.setPadding(new Insets(0, 24, 20, 0)); // Right-aligned with bottom padding

        // Back — returns to main menu without saving any changes
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> {
            WindowManager.switchTo(new MainMenuWindow()); // Discard changes, go back
        });

        // Save — applies and persists the selected theme, then returns to main menu
        Button saveButton = new Button("Save");
        saveButton.setOnAction(event -> {
            boolean isLightMode = lightModeButton.isSelected();
            Assets.appPreferences.putBoolean("isLightMode", isLightMode); // Persist theme choice
            Assets.setLightOrDarkMode(isLightMode); // Apply theme immediately
            WindowManager.switchTo(new MainMenuWindow()); // Return to main menu
        });

        bottomButtons.getChildren().addAll(backButton, saveButton);
        root.setBottom(bottomButtons);

        return new Scene(root, Assets.windowWidth, Assets.windowHeight);
    }

    @Override
    public void onShow() {
        WindowManager.getStage().setTitle("Formiquo - Settings");
    }

}