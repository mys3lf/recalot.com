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

package com.recalot.model.data.connections.downloader.flixster;

import com.recalot.common.Helper;
import com.recalot.common.communication.*;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.exceptions.NotFoundException;
import com.recalot.model.data.connections.downloader.BaseDownloaderDataSource;

import java.io.*;
import java.util.Date;
import java.util.HashMap;

/**
 * Reads the MovieLens ml-100k, ml-1m, or ml-10M100k data set
 *
 * @author matthaeus.schmedding
 */
public class FlixsterDataSource extends BaseDownloaderDataSource {

    public FlixsterDataSource() {
        super();
    }

    @Override
    public void connect() throws BaseException {
        String source = "flixster";
        File folder = null;
        try {
            folder = downloadData(source, "http://www.cs.ubc.ca/~jamalim/datasets/flixster.zip");

            File trustFile = null;
            File ratingsFile = null;

            String dirPath = "";
            for (File dir : folder.listFiles()) {
                if(dir.getName().toLowerCase().equals("flixster-dataset")) {
                    for (File dir2 : dir.listFiles()) {
                        if (dir2.getName().toLowerCase().equals("data")) {
                            for (File file : dir2.listFiles()) {
                                String name = file.getName().toLowerCase();

                                if (name.toLowerCase().equals("nodes.csv")) ratingsFile = file;
                                else if (name.toLowerCase().equals("edges.csv")) trustFile = file;
                            }
                        }
                    }
                }
            }

            if (trustFile != null && ratingsFile != null) {
                setInfo("Read Interactions");
                readRatingsFile(ratingsFile);
                setInfo("Read Trust");
                readTrustFile(trustFile);
            } else {
                throw new NotFoundException("Can not find necessary files in '%s'.", dirPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        setInfo("Done");
    }

    private void readRatingsFile(File file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null) {

                //[user-id, item-id, rating-value]
                String[] split = line.split(" ");

                if (split.length == 3) {
                    String ratingId = "" + i++;

                    String userId = split[0];
                    String itemId = split[1];

                    if (!users.containsKey(InnerIds.getNextId(userId, Helper.Keys.UserId))) {
                        users.put(InnerIds.getNextId(userId, Helper.Keys.UserId), new User(userId));
                    }

                    if (!items.containsKey(InnerIds.getNextId(itemId, Helper.Keys.ItemId))) {
                        items.put(InnerIds.getNextId(itemId, Helper.Keys.ItemId), new Item(itemId));
                    }

                    interactions.put(InnerIds.getNextId(ratingId, Helper.Keys.InteractionId), new Interaction(ratingId, userId, itemId, new Date(), "rating".intern(), split[2].intern(), null));
                }
            }
        } catch (IOException x) {
            x.printStackTrace();
        }
    }

    private void readTrustFile(File file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            int i = 0;
            String line;
            while ((line = reader.readLine()) != null) {

                // [user-id (trustor), user-id (trustee), trust-value]
                String[] split = line.split(" ");

                if (split.length == 3) {
                    String trustor = split[0];
                    String trustee = split[1];
                    String trustValue = split[2];

                    HashMap<String, String> content = new HashMap<>();
                    content.put(Helper.Keys.Value, trustValue);

                    String id = "" + i++;
                    relations.put(InnerIds.getNextId(id, Helper.Keys.RelationId), new Relation(id, trustor, trustee, "trust".intern(), content));
                }
            }
        } catch (IOException x) {
            x.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
    }
}
