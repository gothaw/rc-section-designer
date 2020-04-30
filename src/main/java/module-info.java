module com.radsoltan {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens com.radsoltan to javafx.fxml;
    opens com.radsoltan.controllers to javafx.fxml;

    exports com.radsoltan;
}