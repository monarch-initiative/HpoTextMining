package org.monarchinitiative.hpotextmining.core;

/**
 * A checked exception thrown by HPO Text Mining widgets.
 */
public class HpoTextMiningException extends Exception {
    public HpoTextMiningException() {
        super();
    }

    public HpoTextMiningException(String message) {
        super(message);
    }

    public HpoTextMiningException(String message, Throwable cause) {
        super(message, cause);
    }

    public HpoTextMiningException(Throwable cause) {
        super(cause);
    }

    protected HpoTextMiningException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
