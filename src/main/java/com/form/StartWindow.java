package com.form;

import javafx.scene.Scene;

// Interface that all app windows/scenes must implement
// Ensures every window can build its scene and respond when shown
public interface StartWindow {
    Scene buildScene();  // Builds and returns the scene for this window
    void onShow();       // Called every time this window becomes the active scene
}
