module org.monarchinitiative.hpotextmining.gui {
    requires transitive org.monarchinitiative.hpotextmining.core;
    requires transitive org.monarchinitiative.phenol.core;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires jdk.jsobject;
    requires org.slf4j;
    requires org.controlsfx.controls;

    exports org.monarchinitiative.hpotextmining.gui.controller;
    opens org.monarchinitiative.hpotextmining.gui.controller to javafx.fxml;
}