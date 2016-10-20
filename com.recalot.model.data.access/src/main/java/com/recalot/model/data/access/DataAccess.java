// Copyright (C) 2016 Matthäus Schmedding
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

package com.recalot.model.data.access;


import com.recalot.common.GenericServiceListener;
import com.recalot.common.Helper;
import com.recalot.common.builder.DataSourceBuilder;
import com.recalot.common.exceptions.AlreadyExistsException;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.exceptions.MissingArgumentException;
import com.recalot.common.exceptions.NotFoundException;
import com.recalot.common.communication.Message;
import com.recalot.common.interfaces.model.data.DataInformation;
import com.recalot.common.interfaces.model.data.DataSource;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.BundleContext;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author matthaeus.schmedding
 */
@Component
@Service(DataAccess.class)
public class DataAccess implements com.recalot.common.interfaces.model.data.DataAccess, Closeable {

    private BundleContext context;
    private GenericServiceListener<DataSourceBuilder> dataSourceBuilderListener;
    private ConcurrentHashMap<String, DataSource> dataSources;
    private ConcurrentHashMap<String, Thread> threads;


    @Override
    public void close() throws IOException {
        if (threads != null) {
            threads.values().forEach(java.lang.Thread::interrupt);
        }

        if (dataSourceBuilderListener != null) {
            this.context.removeServiceListener(dataSourceBuilderListener);
        }

        if (dataSources != null) {
            for (DataSource s : dataSources.values()) {
                s.close();
            }
        }
    }

    @Override
    public DataSource getDataSource(String id) throws BaseException {
        // Lock list and return data source object.
        synchronized (dataSources) {
            if (dataSources.containsKey(id)) {
                return dataSources.get(id);
            }
        }

        throw new NotFoundException(String.format("Data source with id %s not found.", id));
    }

    @Override
    public DataSourceBuilder getDataSourceBuilder(String id) throws BaseException {
        return dataSourceBuilderListener.getInstance(id);
    }

    public DataSource tryGetDataSource(String id) throws BaseException {
        // Lock list and return data source object.
        synchronized (dataSources) {
            if (dataSources.containsKey(id)) {
                return dataSources.get(id);
            }
        }

        return null;
    }

    @Override
    public Message updateDataSource(String id, Map<String, String> content) throws BaseException {
        // Lock list and modify data source object.
        synchronized (dataSources) {
            if (dataSources.containsKey(id)) {

                if (threads.containsKey(id)) {
                    threads.get(id).interrupt();
                    threads.remove(id);
                }

                dataSources.put(id, connectDataSource(content));
                return new Message("Update successful", String.format("Data source with id %s successful updated.", id), Message.Status.INFO);
            }
        }

        throw new NotFoundException(String.format("Data source with id %s not found.", id));
    }

    @Override
    public Message deleteDataSource(String id) throws BaseException {
        // Lock list and add data source object.
        synchronized (dataSources) {
            if (dataSources.containsKey(id)) {
                dataSources.remove(id);

                if (threads.containsKey(id)) {
                    threads.get(id).interrupt();
                    threads.remove(id);
                }
                return new Message("Delete successful", String.format("Data source with id %s successful deleted.", id), Message.Status.INFO);
            }
        }

        throw new NotFoundException(String.format("Data source with id %s not found.", id));
    }

    @Override
    public DataSource createDataSource(Map<String, String> content) throws BaseException {
        String typeKey = content.get(Helper.Keys.DataBuilderId);
        String dataSourceId = content.get(Helper.Keys.SourceId);

        if (typeKey == null)
            throw new MissingArgumentException(String.format("Argument %s is missing.", Helper.Keys.DataBuilderId));
        if (dataSourceId == null)
            throw new MissingArgumentException(String.format("Argument %s is missing.", Helper.Keys.SourceId));
        if (tryGetDataSource(dataSourceId) != null)
            throw new AlreadyExistsException(String.format("Data source with id %s already exists.", dataSourceId));

        DataSource dataSource = connectDataSource(content);

        addDataSource(dataSourceId, dataSource);
        return dataSource;
    }

    private DataSource connectDataSource(Map<String, String> content) throws BaseException {
        String typeKey = content.get(Helper.Keys.DataBuilderId);
        String dataSourceId = content.get(Helper.Keys.SourceId);

        if (typeKey == null)
            throw new MissingArgumentException(String.format("Argument %s is missing.", Helper.Keys.DataBuilderId));
        if (dataSourceId == null)
            throw new MissingArgumentException(String.format("Argument %s is missing.", Helper.Keys.SourceId));


        DataSourceBuilder connection = dataSourceBuilderListener.getInstance(typeKey);

        DataSource source = connection.createInstance(dataSourceId, content);
        source.setState(DataInformation.DataState.CONNECTING);

        Thread thread = new Thread() {
            public void run() {
                try {
                    source.connect();
                    source.setState(DataInformation.DataState.READY);
                    threads.remove(source.getId());
                } catch (BaseException e) {
                    e.printStackTrace();
                    threads.remove(source.getId());
                    dataSources.remove(source.getId());
                }
            }
        };

        threads.put(dataSourceId, thread);

        thread.start();

        return source;
    }

    private void addDataSource(String id, DataSource dataSource) {
        // Lock list and add data source object.
        synchronized (dataSources) {
            dataSources.put(id, dataSource);

            //  System.out.println("Add data source with id " + id);
        }
    }

    @Override
    public List<DataInformation> getDataInformations() throws BaseException {
        List<DataInformation> list = new ArrayList<>();
        list.addAll(dataSources.values());
        list.addAll(dataSourceBuilderListener.getAll());

        return list;
    }

    @Override
    public String getKey() {
        return "data-access";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Activate
    private void activate(final BundleContext bundleContext) {
        this.context = bundleContext;
        this.dataSourceBuilderListener = new GenericServiceListener<>(bundleContext, DataSourceBuilder.class.getName());
        this.dataSources = new ConcurrentHashMap<>();
        this.threads = new ConcurrentHashMap<>();
        this.context.addServiceListener(dataSourceBuilderListener);
    }
}
