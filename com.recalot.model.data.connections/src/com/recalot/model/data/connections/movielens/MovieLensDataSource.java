package com.recalot.model.data.connections.movielens;

import com.recalot.common.communication.*;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.exceptions.NotFoundException;
import com.recalot.model.data.connections.base.DataSourceBase;

import java.io.*;
import java.util.*;

/**
 * Reads the MovieLens ml-100k, ml-1m, or ml-10M100k data set
 *
 * @author matthaeus.schmedding
 */
public class MovieLensDataSource extends DataSourceBase {

    private File dir;

    private final String Gender = "Gender".intern();
    private final String Age = "Age".intern();
    private final String Occupation = "Occupation".intern();
    private final String ZipCode = "ZipCode".intern();
    private final String Title = "Title".intern();
    private final String Year = "Year".intern();
    private final String Genres = "Genres".intern();
    private final String Rating = "Rating".intern();

    private DataSet dataSet;

    public MovieLensDataSource() {
        super();
    }

    public void setDir(String dir) {
        this.dir = new File(dir);
    }

    public void connect() throws BaseException {

        // check whether it is the ml-100k, ml-1m, or ml-10M100k folder

        File uDataFile = null;
        File uItemFile = null;
        File uUserFile = null;

        File moviesFile = null;
        File ratingsFile = null;
        File usersFile = null;

        for (File file : dir.listFiles()) {
            String name = file.getName().toLowerCase();

            if (name.equals("users.dat")) usersFile = file;
            else if (name.equals("ratings.dat")) ratingsFile = file;
            else if (name.toLowerCase().equals("movies.dat")) moviesFile = file;
            else if (name.toLowerCase().equals("u.data")) uDataFile = file;
            else if (name.toLowerCase().equals("u.item")) uItemFile = file;
            else if (name.toLowerCase().equals("u.user")) uUserFile = file;
        }


        if (uDataFile != null && uItemFile != null && uUserFile != null) {   //ml-100k
            readSmallUsersFile(uUserFile);
            readSmallMoviesFile(uItemFile);
            readSmallRatingsFile(uDataFile);
        } else if (moviesFile != null && ratingsFile != null && usersFile != null) {   //ml-1m
            readUsers(usersFile);
            readMovies(moviesFile);
            readRatings(ratingsFile);
        } else if (moviesFile != null && ratingsFile != null) {   //ml-10M100K
            readMovies(moviesFile);
            readRatings(ratingsFile);
        } else {
            throw new NotFoundException("Can not find necessary files in '%s'.", dir.getAbsolutePath());
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

                    if (!users.containsKey(userId)) {
                        users.put(userId, new User(userId));
                    }

                    interactions.put(ratingId, new com.recalot.common.communication.Interaction(ratingId, userId, itemId, date, "", split[2].intern(), map));
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

                    items.put(itemId, new com.recalot.common.communication.Item(itemId, map));
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


                    users.put(userId, new User(userId, map));
                }
            }
        } catch (IOException x) {
            x.printStackTrace();
        }
    }

    private void readRatings(File file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line = null;
            int i = 0;
            while ((line = reader.readLine()) != null) {

                //UserID::MovieID::Rating::Timestamp
                String[] split = line.split("::");

                if (split.length == 4) {

                    String ratingId = (split[0] + split[1] + split[2]).intern();

                    String userId = split[0].intern();
                    String itemId = split[1].intern();

                    Date date = new Date(Long.parseLong(split[3]));

                    HashMap<String, String> map = new HashMap<>();

                    map.put(Rating, split[2].intern());

                    if (!users.containsKey(userId)) {
                        users.put(userId, new User(userId));
                    }

                    interactions.put(ratingId, new com.recalot.common.communication.Interaction(ratingId, userId, itemId, date, "", split[2].intern(), map));
                }
            }
        } catch (IOException x) {
            x.printStackTrace();
        }
    }

    private void readMovies(File file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                //MovieID::Title::Genres
                String[] split = line.split("::");
                if (split.length == 3) {
                    String itemId = split[0].intern();

                    HashMap<String, String> map = new HashMap<>();

                    String title = split[1].intern();
                    String year = title.substring(title.lastIndexOf("(") + 1, title.lastIndexOf(")")).intern();

                    map.put(Title, title);
                    map.put(Year, year);

                    map.put(Genres, split[2]);

                    items.put(itemId, new com.recalot.common.communication.Item(itemId, map));
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


                    users.put(userId, new com.recalot.common.communication.User(userId, map));
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
