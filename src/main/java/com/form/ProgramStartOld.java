// package com.form;

// // Imports
// import javafx.application.Application;
// import javafx.geometry.Pos;
// import javafx.geometry.Rectangle2D;
// import javafx.scene.Scene;
// import javafx.scene.control.Button;
// import javafx.scene.layout.BorderPane;
// import javafx.scene.text.Text;
// import javafx.stage.Screen;
// import javafx.stage.Stage;

// public class ProgramStartOld extends Application {
//     Rectangle2D screenBounds = Screen.getPrimary().getBounds();
//     double screenWidth = screenBounds.getWidth();
//     double screenHeight = screenBounds.getHeight();



//     @Override
//     public void start(Stage primaryStage) {
//         // Stage (window) setup
//         // primaryStage.setTitle("Formiquo"); // Sets stage name
//         boolean isFirstLaunch = AssetsOld.appPreferences.getBoolean("isFirstLaunch", true); // get the preference isFirstLaunch

//         BorderPane primarRoot = new BorderPane(); // Root layout container
//         AssetsOld.mainWindowRoot = primarRoot; // adds the current root to mainWindowRoot for future reference
//         AssetsOld.setFont(AssetsOld.mainWindowRoot, "/com/form/RobotoFonts/Roboto_Condensed-medium.ttf", 14);

//         Scene scene = new Scene(AssetsOld.mainWindowRoot, (screenWidth / 3), (screenHeight / 3)); // Create scene with root and size
//         primaryStage.setScene(scene);

//         // If this launch is the first launch of the app
//         if (isFirstLaunch) { // True
//             AssetsOld.appPreferences.putBoolean("isFirstLaunch", false);
//             // Button resetButton = new Button("First Time");
//             // resetButton.setOnAction(event -> {
//             //     Assets.appPreferences.putBoolean("isFirstLaunch", true);
//             // });
//             // Assets.mainWindowRoot.setCenter(resetButton);
//             // BorderPane.setAlignment(resetButton, Pos.CENTER);
            
//             // Adds objects to te welcome window
//             //
//             // Welcome text title
//             Text welcomeTextTitle = new Text("Welcome to Formiquo");
//             AssetsOld.mainWindowRoot.setCenter(welcomeTextTitle);
//             BorderPane.setAlignment(welcomeTextTitle, Pos.CENTER);
//             // Text that shows how to change the settings
//             Text setSettingsText = new Text("System Settings");
//             AssetsOld.mainWindowRoot.setCenter(setSettingsText);
//             BorderPane.setAlignment(setSettingsText, Pos.CENTER);
//             Button continueToMainPageButton = new Button("Continue to Main Menu");
//             continueToMainPageButton.setOnAction(event -> {
//                 // ACTION HERE
//             });
//             AssetsOld.mainWindowRoot.setBottom(continueToMainPageButton);
//             BorderPane.setAlignment(continueToMainPageButton, Pos.CENTER);
//             // ADD CLASS CALL HERE
//         } else { // False
//             Button resetButton = new Button("Reset");
//             resetButton.setOnAction(event -> {
//                 AssetsOld.appPreferences.putBoolean("isFirstLaunch", true);
//             });
//             AssetsOld.mainWindowRoot.setCenter(resetButton);
//             // ADD CLASS CALL HERE
//         }


//         primaryStage.show(); // Display the stage
//     }

//     public static void main(String[] args) {
//         launch(args); // Launches the JavaFX application
//     }
// }
