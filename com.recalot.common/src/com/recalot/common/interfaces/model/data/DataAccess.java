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

import com.recalot.common.builder.DataSourceBuilder;
import com.recalot.common.communication.Message;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.communication.Service;

import java.util.List;
import java.util.Map;

/**
 * @author matthaeus.schmedding
 */
public interface DataAccess  extends Service {

    public DataSource getDataSource(String id) throws BaseException;
    public DataSourceBuilder getDataSourceBuilder(String id) throws BaseException;
    public Message updateDataSource(String id, Map<String, String> content) throws BaseException;
    public Message deleteDataSource(String id)  throws BaseException;
    public DataSource createDataSource(Map<String, String> content)  throws BaseException;
    public List<DataInformation> getDataInformations()  throws BaseException;
}
