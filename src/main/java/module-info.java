module com.example.aukmusic {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires javafx.media;
    requires mp3agic;

    opens com.example.aukmusic to javafx.fxml;
    opens AUKmusic.playlist to javafx.fxml;
    opens AUKmusic.song to javafx.fxml;
    exports AUKmusic;
}