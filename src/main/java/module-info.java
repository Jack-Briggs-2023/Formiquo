module com.form {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;

    opens com.form to javafx.fxml;
    exports com.form;
}