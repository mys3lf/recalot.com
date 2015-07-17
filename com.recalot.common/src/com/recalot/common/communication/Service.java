package com.recalot.common.communication;

import java.io.Closeable;

/**
 * Default construct of a service
 *
 * @author Matthäus Schmedding (info@recalot.com)
 */
public interface Service extends Closeable {
    /**
     *
     * @return service key
     */
    public String getKey();

    /**
     *
     * @return service description
     */
    public String getDescription();
}

