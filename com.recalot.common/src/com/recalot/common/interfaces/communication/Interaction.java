package com.recalot.common.interfaces.communication;

import java.util.Date;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public interface Interaction {
    public String getUserId();
    public String getItemId();
    public Date getTimeStamp();
    public String getType();
}
