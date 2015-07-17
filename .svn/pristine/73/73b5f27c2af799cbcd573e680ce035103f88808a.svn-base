package com.recalot.model.data.connections.movielens;

import com.recalot.common.communication.*;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.exceptions.NotFoundException;
import com.recalot.common.exceptions.NotSupportedException;
import com.recalot.common.interfaces.model.data.DataSource;
import com.recalot.model.data.connections.base.DataSourceBase;

import java.io.*;
import java.util.*;

/**
 * @author matthaeus.schmedding
 */
public class MovieLensDataSource extends DataSourceBase {

    private File dir;
    private File moviesFile = null;
    private File ratingsFile = null;
    private File usersFile = null;

    private final String Gender = "Gender".intern();
    private final String Age = "Age".intern();
    private final String Occupation = "Occupation".intern();
    private final String ZipCode = "ZipCode".intern();
    private final String Title = "Title".intern();
    private final String Year = "Year".intern();
    private final String Genres = "Genres".intern();
    private final String Rating = "Rating".intern();

    private DataSet dataSet;

    public MovieLensDataSource(){
        super();
    }

    public void setDir(String dir){
        this.dir = new File(dir);
    }

    public void connect() throws BaseException {
        for (File file : dir.listFiles()) {
            String name = file.getName().toLowerCase();

            if (name.equals("users.dat")) usersFile = file;
            else if (name.equals("ratings.dat")) ratingsFile = file;
            else if (name.toLowerCase().equals("movies.dat")) moviesFile = file;
        }

        if (usersFile == null)
            throw new NotFoundException("File users.dat cannot be found in directory '%s'.", dir.getAbsolutePath());
        if (ratingsFile == null)
            throw new NotFoundException("File ratings.dat cannot be found in directory '%s'.", dir.getAbsolutePath());
        if (moviesFile == null)
            throw new NotFoundException("File movies.dat cannot be found in directory '%s'.", dir.getAbsolutePath());

        readUsers(usersFile);
        readMovies(moviesFile);
        readRatings(ratingsFile);
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
                    map.put(ZipCode,split[4].intern());


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
