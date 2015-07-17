package com.recalot.common.builder;

import com.recalot.common.Helper;
import com.recalot.common.configuration.ConfigurationItem;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.data.DataInformation;
import com.recalot.common.interfaces.model.data.DataSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Is used for the initialization of DataSources
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class DataSourceBuilder extends InstanceBuilder<DataSource> implements DataInformation {

    /**
     * Initialize the DataSourceBuilder and the default configuration
     *
     * @param initiator the instance that initialize the default instance of the datasource
     * @param className the class name of the data source instance
     * @param key the key of the data source builder
     * @param description the description data source builder
     * @throws BaseException
     */
    public DataSourceBuilder(Initiator initiator, String className, String key, String description) throws BaseException {
        super(initiator, className, key, description);

        setConfiguration(new ConfigurationItem(Helper.Keys.SourceId, ConfigurationItem.ConfigurationItemType.String, "", ConfigurationItem.ConfigurationItemRequirementType.Required));
        setConfiguration(new ConfigurationItem(Helper.Keys.DataBuilderId, ConfigurationItem.ConfigurationItemType.String, "", ConfigurationItem.ConfigurationItemRequirementType.Required));
    }

    /**
     *
     * @return the current state (always "AVAILABLE")
     */
    @Override
    public DataInformation.DataState getState() {
        return DataInformation.DataState.AVAILABLE;
    }

    /**
     *
     * @return the id of the data source builder
     */
    @Override
    public String getId() {
        return "data-builder-" + getKey();
    }
}
