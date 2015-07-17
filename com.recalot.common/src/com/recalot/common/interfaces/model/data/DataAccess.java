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
