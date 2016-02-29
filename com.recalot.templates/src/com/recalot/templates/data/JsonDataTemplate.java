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

package com.recalot.templates.data;

import com.recalot.common.builder.DataSourceBuilder;
import com.recalot.common.communication.*;
import com.recalot.common.communication.TemplateResult;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.data.DataInformation;
import com.recalot.common.interfaces.model.data.DataSource;
import com.recalot.common.interfaces.template.*;
import com.recalot.templates.base.JsonBaseTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author matthaeus.schmedding
 */
public class JsonDataTemplate extends JsonBaseTemplate implements DataTemplate {

    public JsonDataTemplate(){
        super();
    }

    @Override
    public TemplateResult transform(DataSet dataSet) {
        String result = getSerializer().serialize(dataSet);

        return new TemplateResult(200, MimeType, new ByteArrayInputStream(result.getBytes(charset)), charset);
    }

    @Override
    public TemplateResult transform(DataInformation connection) {
        String result = getSerializer().include("id", "state", "info").exclude("*").serialize(connection);

        return new TemplateResult(200, MimeType, new ByteArrayInputStream(result.getBytes(charset)), charset);
    }

    @Override
    public TemplateResult transform(DataSourceBuilder source) throws BaseException {
        String result = getSerializer().include("id", "state", "configuration", "configuration.*").serialize(source);

        return new TemplateResult(200, MimeType, new ByteArrayInputStream(result.getBytes(charset)), charset);
    }

    @Override
    public TemplateResult transform(Interaction[] interactions) {
        String result = getSerializer().serialize(interactions);

        return new TemplateResult(200, MimeType, new ByteArrayInputStream(result.getBytes(charset)), charset);
    }

    @Override
    public TemplateResult transform(Interaction interaction) {
        String result = getSerializer().serialize(interaction);

        return new TemplateResult(200, MimeType, new ByteArrayInputStream(result.getBytes(charset)), charset);
    }

    @Override
    public TemplateResult transform(Item[] items) {
        String result = getSerializer().serialize(items);

        return new TemplateResult(200, MimeType, new ByteArrayInputStream(result.getBytes(charset)), charset);
    }

    @Override
    public TemplateResult transform(Item item) {
        String result = getSerializer().serialize(item);

        return new TemplateResult(200, MimeType, new ByteArrayInputStream(result.getBytes(charset)), charset);
    }

    @Override
    public TemplateResult transform(User[] users) {
        String result = getSerializer().serialize(users);

        return new TemplateResult(200, MimeType, new ByteArrayInputStream(result.getBytes(charset)), charset);
    }

    @Override
    public TemplateResult transform(Relation relation) throws BaseException {
        String result = getSerializer().serialize(relation);

        return new TemplateResult(200, MimeType, new ByteArrayInputStream(result.getBytes(charset)), charset);
    }

    @Override
    public TemplateResult transform(Relation[] relations) throws BaseException {
        String result = getSerializer().serialize(relations);

        return new TemplateResult(200, MimeType, new ByteArrayInputStream(result.getBytes(charset)), charset);
    }

    @Override
    public TemplateResult transform(User user) {
        String result = getSerializer().serialize(user);

        return new TemplateResult(200, MimeType, new ByteArrayInputStream(result.getBytes(charset)), charset);
    }

    @Override
    public TemplateResult transform(List<DataInformation> sources)
    {
        String result = getSerializer().include("id", "state").exclude("*").serialize(sources);

        return new TemplateResult(200, MimeType, new ByteArrayInputStream(result.getBytes(charset)), charset);
    }

    @Override
    public TemplateResult transform(DataSource source) {
        String result = getSerializer().include("id", "dataSourceId", "dataBuilderId", "state", "usersCount", "itemsCount", "interactionsCount", "info").exclude("*").serialize(source);

        return new TemplateResult(200, MimeType, new ByteArrayInputStream(result.getBytes(charset)), charset);
    }

    @Override
    public void close() throws IOException {
    }
}
