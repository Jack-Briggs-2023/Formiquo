package com.form;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class WelcomeWindow implements StartWindow {

    @Override
    public Scene buildScene() {
        BorderPane root = new BorderPane();
        Assets.mainWindowRoot = root; // Update shared root reference
        Assets.setFont(root, "/com/form/RobotoFonts/Roboto_Condensed-medium.ttf", 14);

        // --- Top: app branding ---
        VBox topContent = new VBox(6); // 6px gap between title and tagline
        topContent.setAlignment(Pos.CENTER);
        topContent.setPadding(new Insets(30, 0, 0, 0));

        Text appTitle = new Text("Formiquo");
        Text appTagline = new Text("Build your resume in minutes");

        topContent.getChildren().addAll(appTitle, appTagline);
        root.setTop(topContent);

        // --- Center: theme selection ---
        VBox centerContent = new VBox(12); // 12px gap between elements
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setPadding(new Insets(20));

        Text themeLabel = new Text("Choose your preferred theme:");

        ToggleGroup themeToggleGroup = new ToggleGroup();

        RadioButton lightModeButton = new RadioButton("Light Mode");
        lightModeButton.setToggleGroup(themeToggleGroup);
        lightModeButton.setSelected(true); // Default to light mode

        RadioButton darkModeButton = new RadioButton("Dark Mode");
        darkModeButton.setToggleGroup(themeToggleGroup);

        // Load saved preference in case user somehow lands here again
        boolean savedIsLightMode = Assets.appPreferences.getBoolean("isLightMode", true);
        lightModeButton.setSelected(savedIsLightMode);
        darkModeButton.setSelected(!savedIsLightMode);

        HBox themeOptions = new HBox(20, lightModeButton, darkModeButton); // 20px gap between buttons
        themeOptions.setAlignment(Pos.CENTER);

        centerContent.getChildren().addAll(themeLabel, themeOptions);
        root.setCenter(centerContent);

        // --- Bottom: continue button ---
        Button continueButton = new Button("Continue to Main Menu");
        continueButton.setOnAction(event -> {
            boolean isLightMode = lightModeButton.isSelected();
            Assets.appPreferences.putBoolean("isLightMode", isLightMode); // Save theme preference
            Assets.setLightOrDarkMode(isLightMode); // Apply theme
            WindowManager.switchTo(new MainMenuWindow()); // Switch to main menu
        });
        root.setBottom(continueButton);
        BorderPane.setAlignment(continueButton, Pos.CENTER);
        BorderPane.setMargin(continueButton, new Insets(0, 0, 24, 0)); // Bottom padding

        return new Scene(root, Assets.windowWidth, Assets.windowHeight);
    }

    @Override
    public void onShow() {
        Assets.appPreferences.putBoolean("isFirstLaunch", false); // Mark first launch as complete
        WindowManager.getStage().setTitle("Formiquo - Welcome");
    }

}