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
    public TemplateResult transform(User user) throws BaseException;
    public TemplateResult transform(DataSource source) throws BaseException;
    public TemplateResult transform(DataInformation source) throws BaseException;
    public TemplateResult transform(DataSourceBuilder source) throws BaseException;
    public TemplateResult transform(List<DataInformation> sources) throws BaseException;
}
