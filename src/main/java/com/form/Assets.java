package com.form;

import java.util.prefs.Preferences;

import javafx.geometry.Rectangle2D;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Screen;

public class Assets {

    static BorderPane mainWindowRoot;
    static Preferences appPreferences = Preferences.userNodeForPackage(Assets.class); // app preferences (persists between launches)

    // Shared window size — calculated once from screen bounds, used by all windows
    static Rectangle2D screenBounds = Screen.getPrimary().getBounds();
    static double windowWidth = screenBounds.getWidth() / 3;
    static double windowHeight = screenBounds.getHeight() / 3;

    // Universal font setter
    // Defaults to Roboto Condensed medium if fontFileName is null or empty
    public static void setFont(BorderPane root, String fontFileName, int size) {
        String fontPath;
        if (fontFileName != null && !fontFileName.isEmpty()) {
            fontPath = fontFileName;
        } else {
            fontPath = "/com/form/RobotoFonts/Roboto_Condensed-medium.ttf";
        }
        Font defaultFont = Font.loadFont(Assets.class.getResourceAsStream(fontPath), size); // Load custom font from resources

        // If font loads successfully, override default font styling
        if (defaultFont != null) {
            root.setStyle("-fx-font-family: '" + defaultFont.getFamily() + "'; -fx-font-size: " + size + "px;"); // Sets default font and size
        } else {
            System.out.println("Custom font failed to load.");
        }
    }

    // Applies light or dark theme by swapping the scene's CSS stylesheet
    // Font settings from setFont() are unaffected — inline styles beat stylesheets in JavaFX
    public static void setLightOrDarkMode(Boolean isLightMode) {
        javafx.scene.Scene scene = WindowManager.getCurrentScene();
        if (scene == null) return; // No scene active yet — preference is saved and applied on next switchTo

        scene.getStylesheets().clear(); // Remove any previously applied theme

        String themePath = isLightMode
            ? "/com/form/themes/light-theme.css"
            : "/com/form/themes/dark-theme.css";

        java.net.URL themeUrl = Assets.class.getResource(themePath);
        if (themeUrl != null) {
            scene.getStylesheets().add(themeUrl.toExternalForm());
        } else {
            System.out.println("Theme file not found: " + themePath);
        }
    }

}