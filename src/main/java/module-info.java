module com.example.FinalProject {
    requires java.logging;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.media;
    requires javafx.swing;
    requires javafx.graphics;
    requires org.apache.poi.ooxml;
    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    opens com.FinalProject.UMS to javafx.fxml;
    exports com.FinalProject.UMS;
}