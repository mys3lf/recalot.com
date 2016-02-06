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

package com.recalot.model.data.connections.downloader.movielens;

import com.recalot.common.Helper;
import com.recalot.common.communication.*;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.exceptions.NotFoundException;
import com.recalot.model.data.connections.base.DataSourceBase;
import com.recalot.model.data.connections.downloader.BaseDownloaderDataSource;

import java.io.*;
import java.util.*;

/**
 * Reads the MovieLens ml-100k, ml-1m, or ml-10M100k data set
 *
 * @author matthaeus.schmedding
 */
public class MovieLensDataSource extends BaseDownloaderDataSource {

    private final String Gender = "Gender".intern();
    private final String Age = "Age".intern();
    private final String Occupation = "Occupation".intern();
    private final String ZipCode = "ZipCode".intern();
    private final String Title = "Title".intern();
    private final String Year = "Year".intern();
    private final String Genres = "Genres".intern();
    private final String Rating = "Rating".intern();
    private String source;
    private DataSet dataSet;

    public MovieLensDataSource() {
        super();
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public void connect() throws BaseException {

        try {

            if (source != null && !source.isEmpty()) {
                switch (source) {
                    case "ml-1m":
                        initialize1m();
                        break;
                    case "ml-10m":
                        initialize10m();
                        break;
                    case "ml-100k":
                        initialize100k();
                        break;
                    case "ml-20m":
                    default:
                        initializeSource();
                        break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        setInfo("Done");
    }

    private void initialize100k() throws IOException, NotFoundException {
        File folder = downloadData(source, "http://files.grouplens.org/datasets/movielens/ml-100k.zip");

        String folderName = "ml-100k";

        File moviesFile = null;
        File ratingsFile = null;
        File usersFile = null;

        String dirPath = "";
        for (File dir : folder.listFiles()) {
            if (dir.getName().toLowerCase().equals(folderName)) {
                dirPath = dir.getAbsolutePath();

                for (File file : dir.listFiles()) {
                    String name = file.getName().toLowerCase();

                    if (name.toLowerCase().equals("u.data")) ratingsFile = file;
                    else if (name.toLowerCase().equals("u.item")) moviesFile = file;
                    else if (name.toLowerCase().equals("u.user")) usersFile = file;
                }
            }
        }

        if (moviesFile != null && ratingsFile != null && usersFile != null) {   //ml-1m
            setInfo("Read Users");
            readSmallUsersFile(usersFile);
            setInfo("Read Items");
            readSmallMoviesFile(moviesFile);
            setInfo("Read Interactions");
            readSmallRatingsFile(ratingsFile);

        } else {
            throw new NotFoundException("Can not find necessary files in '%s'.", dirPath);
        }
    }

    private void initialize1m() throws IOException, NotFoundException {
        File folder = downloadData(source, "http://files.grouplens.org/datasets/movielens/ml-1m.zip");

        String folderName = "ml-1m";

        File moviesFile = null;
        File ratingsFile = null;
        File usersFile = null;

        String dirPath = "";
        for (File dir : folder.listFiles()) {
            if (dir.getName().toLowerCase().equals(folderName)) {
                dirPath = dir.getAbsolutePath();

                for (File file : dir.listFiles()) {
                    String name = file.getName().toLowerCase();

                    if (name.equals("users.dat")) usersFile = file;
                    else if (name.equals("ratings.dat")) ratingsFile = file;
                    else if (name.toLowerCase().equals("movies.dat")) moviesFile = file;
                }
            }
        }

        if (moviesFile != null && ratingsFile != null && usersFile != null) {   //ml-1m
            setInfo("Read Users");
            readUsers(usersFile);
            setInfo("Read Items");
            readMovies(moviesFile, "::", false);
            setInfo("Read Interactions");
            readRatings(ratingsFile, "::", false);
        } else {
            throw new NotFoundException("Can not find necessary files in '%s'.", dirPath);
        }
    }

    private void initialize10m() throws IOException, NotFoundException {
        File folder = downloadData(source, "http://files.grouplens.org/datasets/movielens/ml-10m.zip");

        String folderName = "ml-10M100K";

        File moviesFile = null;
        File ratingsFile = null;

        String dirPath = "";
        for (File dir : folder.listFiles()) {
            if (dir.getName().equals(folderName)) {
                dirPath = dir.getAbsolutePath();

                for (File file : dir.listFiles()) {
                    String name = file.getName().toLowerCase();

                    if (name.equals("ratings.dat")) ratingsFile = file;
                    else if (name.toLowerCase().equals("movies.dat")) moviesFile = file;
                }
            }
        }

        if (moviesFile != null && ratingsFile != null) {
            setInfo("Read Items");
            readMovies(moviesFile, "::", false);
            setInfo("Read Interactions");
            readRatings(ratingsFile, "::", false);
        } else {
            throw new NotFoundException("Can not find necessary files in '%s'.", dirPath);
        }
    }

    private void initializeSource() throws IOException, NotFoundException {
        File folder = downloadData(source, "http://files.grouplens.org/datasets/movielens/ml-20m.zip");

        File moviesFile = null;
        File ratingsFile = null;
        File usersFile = null;

        String dirPath = "";
        for (File dir : folder.listFiles()) {
            if (dir.getName().toLowerCase().equals(source)) {
                dirPath = dir.getAbsolutePath();

                for (File file : dir.listFiles()) {
                    String name = file.getName().toLowerCase();

                    if (name.equals("ratings.csv")) ratingsFile = file;
                    else if (name.toLowerCase().equals("movies.csv")) moviesFile = file;
                }
            }
        }

        if (moviesFile != null && ratingsFile != null) {
            setInfo("Read Items");
            readMovies(moviesFile, ",", true);
            setInfo("Read Interactions");
            readRatings(ratingsFile, ",", true);
        } else {
            throw new NotFoundException("Can not find necessary files in '%s'.", dirPath);
        }
    }


    private void readSmallRatingsFile(File file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line = null;
            int i = 0;
            while ((line = reader.readLine()) != null) {

                //UserID::MovieID::Rating::Timestamp
                String[] split = line.split("\t");

                if (split.length == 4) {

                    String ratingId = (split[0] + split[1] + split[2]).intern();

                    String userId = split[0].intern();
                    String itemId = split[1].intern();

                    Date date = new Date(Long.parseLong(split[3]));

                    HashMap<String, String> map = new HashMap<>();

                    map.put(Rating, split[2].intern());

                    if (!users.containsKey(InnerIds.getNextId(userId, Helper.Keys.UserId))) {
                        users.put(InnerIds.getNextId(userId, Helper.Keys.UserId), new User(userId));
                    }

                    interactions.put(InnerIds.getNextId(ratingId, Helper.Keys.InteractionId), new com.recalot.common.communication.Interaction(ratingId, userId, itemId, date, "rating".intern(), split[2].intern(), map));
                }
            }
        } catch (IOException x) {
            x.printStackTrace();
        }
    }

    private void readSmallMoviesFile(File file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line = null;

            List<String> cats = new ArrayList<>();
            cats.add("unknown");
            cats.add("Action");
            cats.add("Adventure");
            cats.add("Animation");
            cats.add("Children's");
            cats.add("Comedy");
            cats.add("Crime");
            cats.add("Documentary");
            cats.add("Drama");
            cats.add("Fantasy");
            cats.add("Film-Noir");
            cats.add("Horror");
            cats.add("Musical");
            cats.add("Mystery");
            cats.add("Romance");
            cats.add("Sci-Fi");
            cats.add("Thriller");
            cats.add("War");
            cats.add("Western");


            while ((line = reader.readLine()) != null) {
                // movie id | movie title | release date | video release date | IMDb URL | unknown | Action | Adventure | Animation | Children's | Comedy | Crime | Documentary | Drama | Fantasy | Film-Noir | Horror | Musical | Mystery | Romance | Sci-Fi | Thriller | War | Western |
                String[] split = line.split("\\|");
                if (split.length == 24) {
                    String itemId = split[0].intern();

                    HashMap<String, String> map = new HashMap<>();

                    String title = split[1].intern();
                    String year = "";

                    if (title.contains("(")) {
                        year = title.substring(title.lastIndexOf("(") + 1, title.lastIndexOf(")")).intern();
                    }

                    map.put(Title, title);
                    map.put(Year, year);

                    String categories = "";

                    for (int i = 0; i < 19; i++) {
                        if (split[5 + i].equals("1")) {
                            if (categories.length() > 0) categories += "|";
                            categories += cats.get(i);
                        }
                    }
                    map.put(Genres, categories);

                    items.put(InnerIds.getNextId(itemId, Helper.Keys.ItemId), new com.recalot.common.communication.Item(itemId, map));
                }
            }
        } catch (IOException x) {
            x.printStackTrace();
        }
    }

    private void readSmallUsersFile(File file) {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {

            String line = null;
            while ((line = reader.readLine()) != null) {

                //UserID::Gender::Age::Occupation::Zip-code
                String[] split = line.split("\\|");
                if (split.length == 5) {
                    String userId = split[0].intern();

                    HashMap<String, String> map = new HashMap<>();

                    map.put(Gender, split[2].intern());
                    map.put(Age, split[1].intern());
                    map.put(Occupation, split[3].intern());
                    map.put(ZipCode, split[4].intern());


                    users.put(InnerIds.getNextId(userId, Helper.Keys.UserId), new User(userId, map));
                }
            }
        } catch (IOException x) {
            x.printStackTrace();
        }
    }


    private void readUsers(File file) {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {

            String line = null;
            while ((line = reader.readLine()) != null) {

                //UserID::Gender::Age::Occupation::Zip-code
                String[] split = line.split("::");
                if (split.length == 5) {
                    String userId = split[0].intern();

                    HashMap<String, String> map = new HashMap<>();

                    map.put(Gender, split[1].intern());
                    map.put(Age, split[2].intern());
                    map.put(Occupation, split[3].intern());
                    map.put(ZipCode, split[4].intern());


                    users.put(InnerIds.getNextId(userId, Helper.Keys.UserId), new com.recalot.common.communication.User(userId, map));
                }
            }
        } catch (IOException x) {
            x.printStackTrace();
        }
    }

    private void readRatings(File file, String sep, boolean hasHeader) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line = null;

            boolean first = true;
            while ((line = reader.readLine()) != null) {
                if(hasHeader && first) {
                    first = false;
                    continue;
                }
                //UserID::MovieID::Rating::Timestamp
                String[] split = line.split(sep);

                if (split.length == 4) {

                    String ratingId = (split[0] + split[1] + split[2]).intern();

                    String userId = split[0].intern();
                    String itemId = split[1].intern();

                    Date date = new Date(Long.parseLong(split[3]));

                    HashMap<String, String> map = new HashMap<>();

                    map.put(Rating, split[2].intern());

                    if (!users.containsKey(InnerIds.getNextId(userId, Helper.Keys.UserId))) {
                        users.put(InnerIds.getNextId(userId, Helper.Keys.UserId), new User(userId));
                    }

                    interactions.put(InnerIds.getNextId(ratingId, Helper.Keys.InteractionId), new com.recalot.common.communication.Interaction(ratingId, userId, itemId, date, "rating".intern(), split[2].intern(), map));
                }
            }
        } catch (IOException x) {
            x.printStackTrace();
        }
    }

    private void readMovies(File file, String sep, boolean hasHeader) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line = null;
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                if(hasHeader && first) {
                    first = false;
                    continue;
                }
                //MovieID::Title::Genres
                String[] split = line.split(sep);
                if (split.length == 3) {
                    String itemId = split[0].intern();

                    HashMap<String, String> map = new HashMap<>();

                    String title = split[1].intern();
                    if(title.contains("(") && title.contains(")")) {
                        String year = title.substring(title.lastIndexOf("(") + 1, title.lastIndexOf(")")).intern();
                        map.put(Year, year);
                    }

                    map.put(Title, title);

                    map.put(Genres, split[2]);

                    items.put(InnerIds.getNextId(itemId, Helper.Keys.ItemId), new com.recalot.common.communication.Item(itemId, map));
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
