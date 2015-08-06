package com.recalot.common.interfaces.model.experiment;

import com.recalot.common.communication.DataSet;
import com.recalot.common.communication.Service;
import com.recalot.common.configuration.Configurable;
import com.recalot.common.configuration.ConfigurationItem;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.data.DataSource;

/**
 * @author matthaeus.schmedding
 */
public abstract class DataSplitter extends Configurable implements DataSplitterInformation {

    // Use a global split and not a per-user split; could be set to false in later experiments as default
    private boolean globalRandomSplit = false;

    /**
     * Remember the number of folds
     */
    private int nbFolds = 5;

    public void setNbFolds(int nbFolds) {
        this.nbFolds = nbFolds;
    }

    public int getNbFolds() {
        return nbFolds;
    }

    public boolean isGlobalRandomSplit() {
        return globalRandomSplit;
    }

    public void setGlobalRandomSplit(boolean globalRandomSplit) {
        this.globalRandomSplit = globalRandomSplit;
    }

    public abstract DataSet[] split(DataSource source) throws BaseException;


    /**
     * necessary for flexjson
     * @return
     */
    public ConfigurationItem[] getConfiguration() {
        return super.getConfiguration();
    }
}
