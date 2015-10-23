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
    private int nbFolds = 2;

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
