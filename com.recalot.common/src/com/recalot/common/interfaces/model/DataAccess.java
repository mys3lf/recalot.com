package com.recalot.common.interfaces.model;

import com.recalot.common.interfaces.communication.*;

import java.util.Date;
import java.util.HashMap;

/**
 * @author Matthaeus.schmedding
 */
public interface DataAccess extends UserDataAccess, ItemDataAccess, InteractionDataAccess{
    public DataSet getDataSet();
}
