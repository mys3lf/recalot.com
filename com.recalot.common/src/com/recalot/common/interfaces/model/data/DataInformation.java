package com.recalot.common.interfaces.model.data;

/**
 * @author matthaeus.schmedding
 */
public interface DataInformation {

    public DataState getState();
    public String getId();
    public String getInfo();

    public enum DataState {
        AVAILABLE,
        CONNECTING,
        READY
    }
}
