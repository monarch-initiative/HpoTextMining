module org.monarchinitiative.hpotextmining.demo {
    requires org.monarchinitiative.hpotextmining.gui;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.monarchinitiative.phenol.io;
    requires org.slf4j;

    exports org.monarchinitiative.hpotextmining.demo to javafx.graphics;

    opens org.monarchinitiative.hpotextmining.demo to javafx.fxml;
}