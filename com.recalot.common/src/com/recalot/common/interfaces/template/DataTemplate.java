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

package com.recalot.common.interfaces.template;

import com.recalot.common.builder.DataSourceBuilder;
import com.recalot.common.communication.*;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.data.DataInformation;
import com.recalot.common.interfaces.model.data.DataSource;

import java.util.List;

/**
 * @author Matthaeus.schmedding
 */
public interface DataTemplate extends BaseTemplate {
    public TemplateResult transform(DataSet dataSet) throws BaseException;
    public TemplateResult transform(Interaction[] interactions) throws BaseException;
    public TemplateResult transform(Interaction interaction) throws BaseException;
    public TemplateResult transform(Item[] items) throws BaseException;
    public TemplateResult transform(Item item) throws BaseException;
    public TemplateResult transform(User[] users) throws BaseException;
    public TemplateResult transform(Relation relation) throws BaseException;
    public TemplateResult transform(Relation[] relations) throws BaseException;
    public TemplateResult transform(User user) throws BaseException;
    public TemplateResult transform(DataSource source) throws BaseException;
    public TemplateResult transform(DataInformation source) throws BaseException;
    public TemplateResult transform(DataSourceBuilder source) throws BaseException;
    public TemplateResult transform(List<DataInformation> sources) throws BaseException;
}
