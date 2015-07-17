package com.recalot.common.log;

/**
 * Abstract class for instance that can use logging.
 * @author Matthäus Schmedding (info@recalot.com)
 */
public abstract class Loggable {
    /**
     * Logger
     */
    protected LogTracker logger;

    /**
     * Setter for the logger
     * @param logger logger
     */
    public void setLogger(LogTracker logger) {
        this.logger = logger;
    }
}
