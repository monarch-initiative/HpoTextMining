package org.monarchinitiative.hpotextmining.core;

/**
 * An unchecked exception thrown by HPO Text Mining widgets.
 */
public class HpoTextMiningRuntimeException extends RuntimeException {
    public HpoTextMiningRuntimeException() {
        super();
    }

    public HpoTextMiningRuntimeException(String message) {
        super(message);
    }

    public HpoTextMiningRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public HpoTextMiningRuntimeException(Throwable cause) {
        super(cause);
    }

    protected HpoTextMiningRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
