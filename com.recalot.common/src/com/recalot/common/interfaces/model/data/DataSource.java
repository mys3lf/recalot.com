package com.recalot.common.interfaces.model.data;


import com.recalot.common.communication.DataSet;
import com.recalot.common.exceptions.BaseException;

import java.io.Closeable;

/**
 *
 * TODO: add relationship between users
 * @author Matthaeus.schmedding
 */
public abstract class DataSource implements DataInformation, UserDataAccess, ItemDataAccess, InteractionDataAccess, Closeable {

    private DataState state;
    private String dataBuilderId;
    private String id;

    @Override
    public DataState getState() {
        return state;
    }

    public void setState(DataState state) {
        this.state = state;
    }

    public void setDataBuilderId(String id) {
        this.dataBuilderId = id;
    }

    public String getDataBuilderId() {
        return this.dataBuilderId;
    }

    public void setSourceId(String id) {
        setId(id);
    }

    public String getSourceId() {
        return getId();
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public abstract DataSet getDataSet();

    public abstract void connect() throws BaseException;
}
