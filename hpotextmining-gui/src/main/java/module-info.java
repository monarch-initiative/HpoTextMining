module hpotextmining.gui {
    exports org.monarchinitiative.hpotextmining.gui.controller;

    requires transitive hpotextmining.core;
    requires phenol.core;

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.web;

    requires org.controlsfx.controls;

    requires org.slf4j;
    requires jdk.jsobject;
}