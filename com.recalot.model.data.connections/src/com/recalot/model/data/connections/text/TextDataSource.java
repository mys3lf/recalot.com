package com.recalot.model.data.connections.text;

import com.recalot.common.communication.Item;
import com.recalot.common.communication.Relation;
import com.recalot.common.communication.User;
import com.recalot.common.exceptions.BaseException;
import com.recalot.model.data.connections.base.DataSourceBase;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by matthaeus.schmedding on 11.06.2015.
 */
public class TextDataSource extends DataSourceBase {
    private File file;

    public TextDataSource(){
        super();
    }

    @Override
    public void connect() throws BaseException {
        if(this.file != null && this.file.exists()){

            users.put("1", new User("1", new HashMap<>()));
            //TODO read text
            //TODO single user
        }
    }

    @Override
    public void close() throws IOException {

    }

    public void setFile(String file) {
        this.file = new File(file);
    }
}
