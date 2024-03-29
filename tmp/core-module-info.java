module org.monarchinitiative.hpotextmining.core {
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires org.slf4j;

    exports org.monarchinitiative.hpotextmining.core.miners;
    exports org.monarchinitiative.hpotextmining.core.miners.scigraph to com.fasterxml.jackson.databind;
}