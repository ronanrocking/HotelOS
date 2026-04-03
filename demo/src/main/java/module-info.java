module com.example {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.base; // Required for PropertyValueFactory

    opens com.example to javafx.fxml, javafx.base, javafx.graphics;
    exports com.example;
}