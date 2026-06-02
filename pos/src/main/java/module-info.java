module org.example.bytefood {
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires org.kordamp.ikonli.javafx;
    requires java.net.http;
    requires com.fasterxml.jackson.core;
    requires javafx.graphics;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.github.oshi;
    requires java.logging;
    requires com.sun.jna;
    requires com.sun.jna.platform;
    requires org.purejava.secret;
    requires org.freedesktop.dbus;

    exports org.example.posFX.objects;
    exports org.example.posFX;

    opens org.example.posFX.objects to javafx.fxml, com.fasterxml.jackson.databind;
    opens org.example.posFX to javafx.fxml;
    opens org.example.posFX.auth to javafx.fxml, com.fasterxml.jackson.databind;
    opens org.example.posFX.apiCommunication to com.fasterxml.jackson.databind;
    opens org.example.posFX.apiCommunication.order to com.fasterxml.jackson.databind;
    opens org.example.posFX.auth.device to com.fasterxml.jackson.databind, javafx.fxml;
}