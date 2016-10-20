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

package com.recalot.model.data.connections.downloader.ciao;

import com.recalot.common.Helper;
import com.recalot.common.communication.*;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.exceptions.NotFoundException;
import com.recalot.model.data.connections.downloader.BaseDownloaderDataSource;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class CiaoDataSource extends BaseDownloaderDataSource {


    public static String Genre = "genre".intern();

    public CiaoDataSource(){
        super();
    }

    @Override
    public void connect() throws BaseException {
        String source = "ciao";
        File folder = null;
        try {
            folder =  downloadData(source, "http://librec.net/datasets/CiaoDVD.zip");

            File trustFile = null;
            File ratingsFile = null;

            String dirPath = "";
            for (File file : folder.listFiles()) {
                String name = file.getName().toLowerCase();

                if (name.toLowerCase().equals("movie-ratings.txt")) ratingsFile = file;
                else if (name.toLowerCase().equals("trusts.txt")) trustFile = file;
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
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            while ((line = reader.readLine()) != null) {

                //userID, movieID, genreID, reviewID, movieRating, date
                String[] split = line.split(",");

                if (split.length == 6) {
                    String ratingId = "" + i++;

                    String userId = split[0];
                    String itemId = split[1];
                    String genreId = split[2];
                    String rating = split[4];
                    String date = split[5];

                    if (!users.containsKey(InnerIds.getNextId(userId))) {
                        users.put(InnerIds.getNextId(userId), new User(userId));
                    }

                    if (!items.containsKey(InnerIds.getNextId(itemId))) {
                        Map<String, String> content = new HashMap<>();
                        content.put(Genre, genreId);

                        items.put(InnerIds.getNextId(itemId), new Item(itemId, content));
                    }
                    interactions.put(InnerIds.getNextId(ratingId), new Interaction(ratingId, userId, itemId, format.parse(date), "rating".intern(), rating.intern(), null));
                }
            }
        } catch (IOException x) {
            x.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void readTrustFile(File file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            int i = 0;
            String line;
            while ((line = reader.readLine()) != null) {

                // trustorID, trusteeID, trustValue
                String[] split = line.split(",");

                if (split.length == 3) {
                    String trustor = split[0];
                    String trustee = split[1];
                    String trustValue = split[2];

                    HashMap<String, String> content = new HashMap<>();
                    content.put(Helper.Keys.Value, trustValue);

                    String id = "" + i++;
                    relations.put(InnerIds.getNextId(id), new Relation(id, trustor, trustee, "trust".intern(), content));
                }
            }
        } catch (IOException x) {
            x.printStackTrace();
        }
    }

}
