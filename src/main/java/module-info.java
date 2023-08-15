module com.example.asteroids {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens com.JedN.asteroids to javafx.fxml;
    exports com.JedN.asteroids;
}