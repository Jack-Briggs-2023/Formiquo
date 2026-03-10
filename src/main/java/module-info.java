module com.form {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;
    requires javafx.graphics;

    opens com.form to javafx.fxml;
    exports com.form;
}