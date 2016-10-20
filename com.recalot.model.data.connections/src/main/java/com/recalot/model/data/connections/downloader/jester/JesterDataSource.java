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

package com.recalot.model.data.connections.downloader.jester;

import com.recalot.common.communication.*;
import com.recalot.common.configuration.Configuration;
import com.recalot.common.configuration.ConfigurationItem;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.exceptions.NotFoundException;
import com.recalot.model.data.connections.downloader.BaseDownloaderDataSource;

import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

/**
 * Reads the Jester Dataset 1,2, or 3
 *
 * @author matthaeus.schmedding
 */


@Configuration(key = "source", type = ConfigurationItem.ConfigurationItemType.Options, options = {"jester-1", "jester-2", "jester-3", "combined"})
public class JesterDataSource extends BaseDownloaderDataSource {

    private String source;
    private DataSet dataSet;

    public JesterDataSource() {
        super();
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public void connect() throws BaseException {
        File folder;
        try {
            folder = downloadData("jester", "http://recalot.com/downloads/jester-data.zip");

            String fileName = "jester-data-1";
            if (source != null && !source.isEmpty()) {
                switch (source) {
                    case "jester-1":
                        readData(folder, "jester-data-1.csv");

                        break;
                    case "jester-2":
                        readData(folder, "jester-data-2.csv");

                        break;
                    case "jester-3":
                        readData(folder, "jester-data-3.csv");

                        break;
                    default:
                        readAllData(folder);
                        break;
                }
            }


            setInfo("Done");

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void readData(File folder, String fileName) throws IOException, NotFoundException {
        File csvFile = null;
        for (File file : folder.listFiles()) {
            if (file.getName().toLowerCase().equals(fileName)) {
                csvFile = file;
                break;
            }
        }

        if (csvFile != null) {
            setInfo("Read Data");
            readRatingsFile(csvFile, 0);

        } else {
            throw new NotFoundException("Can not find necessary files in '%s'.", folder.getAbsolutePath());
        }
    }

    private void readAllData(File folder) throws IOException, NotFoundException {
        setInfo("Read Data");
        for (File file : folder.listFiles()) {

            if (file.getName().toLowerCase().endsWith(".csv")) {
                readRatingsFile(file, users.size());
            }
        }
    }


    private void readRatingsFile(File file, int startId) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line = null;
            int i = startId;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(";");

                //i == user id
                String userId = "" + i;

                //if value == 99 -> not rated
                for(int j = 1; j < split.length; j++) {
                    //j == item id
                    String itemId= "" + j;
                    if(!items.containsKey(InnerIds.getNextId(itemId))) items.put(InnerIds.getNextId(itemId), new Item(itemId));
                    if(!users.containsKey(InnerIds.getNextId(userId))) users.put(InnerIds.getNextId(userId), new User(userId));

                    NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
                    Number number = format.parse(split[j]);
                    Double value = number.doubleValue();

                    if(!value.isNaN() && value != 99.0) {

                        String ratingId = "" + interactions.size();

                        interactions.put(InnerIds.getNextId(ratingId), new Interaction(ratingId, userId, itemId, null, "rating".intern(), "" + value, new HashMap<>()));
                    }
                }
                i++;
            }
        } catch (IOException x) {
            x.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void close() throws IOException {

    }
}
