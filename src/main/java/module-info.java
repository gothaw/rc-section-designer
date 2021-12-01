module com.radsoltan {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.swing;

    opens com.radsoltan to javafx.fxml;
    opens com.radsoltan.controllers to javafx.fxml;
    opens com.radsoltan.components to javafx.fxml;

    exports com.radsoltan;
    exports com.radsoltan.components;
}