module org.example.weatherapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens org.example.weatherapp to javafx.fxml;
    exports org.example.weatherapp;
}