package com.form;

import java.util.prefs.Preferences;

import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;

public class Assets {

    static BorderPane mainWindowRoot;
    static Preferences appPreferences = Preferences.userNodeForPackage(Assets.class); // add saving preferences to class

    // universal class that sets the font
    // Will defualt to universal font if font path returns error
    public static void setFont(BorderPane root, String FontFileName, int size) {
        String fontPath;
        if (FontFileName != null && !FontFileName.isEmpty()) {
            fontPath = FontFileName;
        } else {
            fontPath = "/com/form/RobotoFonts/Roboto_Condensed-medium.ttf";
        }
        Font defaultFont = Font.loadFont(Assets.class.getResourceAsStream(fontPath), size); // Load custom font from resources

        // If font loads successfully, override default font styling
        if (defaultFont != null) {
            root.setStyle("-fx-font-family: '" + defaultFont.getFamily() + "'; -fx-font-size: " + size + "px;"); // Sets default font, to defaultFont, and size
        } else {
            System.out.println("Custom font failed to load.");
        }
    }

    public static void setLightOrDarkMode(Boolean isLightMode) {
        if (isLightMode) {

        } else {

        }

    }




}
