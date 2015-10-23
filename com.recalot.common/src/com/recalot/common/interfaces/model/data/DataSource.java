// Copyright (C) 2015 Matthäus Schmedding
//
// This file is part of recalot.com.
//
// recalot.com is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// recalot.com is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with recalot.com. If not, see <http://www.gnu.org/licenses/>.

package com.recalot.common.interfaces.model.data;


import com.recalot.common.communication.DataSet;
import com.recalot.common.exceptions.BaseException;

import java.io.Closeable;

/**
 *
 * @author Matthaeus.schmedding
 */
public abstract class DataSource implements DataInformation, RelationDataAccess, UserDataAccess, ItemDataAccess, InteractionDataAccess, Closeable {

    private DataState state;
    private String dataBuilderId;
    private String info;
    private String id;

    @Override
    public DataState getState() {
        return state;
    }

    public void setState(DataState state) {
        this.state = state;
    }

    @Override
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
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
