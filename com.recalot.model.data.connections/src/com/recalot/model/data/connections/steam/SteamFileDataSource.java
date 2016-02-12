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

package com.recalot.model.data.connections.steam;

import com.recalot.common.Helper;
import com.recalot.common.communication.*;
import com.recalot.common.configuration.Configuration;
import com.recalot.common.configuration.ConfigurationItem;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.exceptions.NotFoundException;
import com.recalot.model.data.connections.base.DataSourceBase;

import java.io.*;
import java.util.Date;

/**
 * Reads the steam file
 *
 * @author matthaeus.schmedding
 */

@Configuration(key = "dir")
@Configuration(key = "interaction", type = ConfigurationItem.ConfigurationItemType.Options, options = {"own", "played"})
public class SteamFileDataSource extends DataSourceBase {

    private String dir;
    private String interaction;

    public SteamFileDataSource() {
        super();
    }

    @Override
    public void connect() throws BaseException {
        String source = "flixster";
        File folder = null;
        folder = new File(dir);

        File gamesFile = null;
        File friendshipFile = null;
        File usersFile = null;
        File playedFile = null;
        File ownedFile = null;


        for (File file : folder.listFiles()) {
            switch (file.getName().toLowerCase()) {
                case "friendship.csv":
                    friendshipFile = file;
                    break;
                case "games.csv":
                    gamesFile = file;
                    break;
                case "own.csv":
                    ownedFile = file;
                    break;
                case "played.csv":
                    playedFile = file;
                    break;
                case "users.csv":
                    usersFile = file;
                    break;

            }
        }

        if (gamesFile != null && friendshipFile != null && usersFile != null && playedFile != null && ownedFile != null) {
            setInfo("Read Users");
            readUsers(usersFile);
            setInfo("Read Games");
            readGames(usersFile);
            setInfo("Read Friendship");
            readFriendship(friendshipFile);
            setInfo("Read Interactions");
            readRatingsFile(interaction.equals("own") ? ownedFile : playedFile);
        } else {
            throw new NotFoundException("Can not find necessary files in '%s'.", dir);
        }

        setInfo("Done");
    }

    private void readRatingsFile(File file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null) {

                //[user-id, item-id, rating-value]
                String[] split = line.split(";");

                if (split.length >= 2) {
                    String ratingId = "" + i++;

                    String userId = split[0].intern();
                    String itemId = split[1].intern();

                    String rating = split.length > 2 ? split[2].intern() : "1";


                    if (!users.containsKey(InnerIds.getNextId(userId, Helper.Keys.UserId))) {
                        users.put(InnerIds.getNextId(userId, Helper.Keys.UserId), new User(userId));
                    }

                    if (!items.containsKey(InnerIds.getNextId(itemId, Helper.Keys.ItemId))) {
                        items.put(InnerIds.getNextId(itemId, Helper.Keys.ItemId), new Item(itemId));
                    }

                    interactions.put(InnerIds.getNextId(ratingId, Helper.Keys.InteractionId), new Interaction(ratingId, userId, itemId, new Date(), "rating".intern(), rating, null));
                }
            }
        } catch (IOException x) {
            x.printStackTrace();
        }
    }

    private void readFriendship(File file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            int i = 0;
            String line;
            while ((line = reader.readLine()) != null) {

                // [user-id (trustor), user-id (trustee), trust-value]
                String[] split = line.split(";");

                if (split.length == 2) {
                    String user1 = split[0].intern();
                    String user2 = split[1].intern();

                    String id = "" + i++;
                    relations.put(InnerIds.getNextId(id, Helper.Keys.RelationId), new Relation(id, user1, user2, "friendship".intern()));
                }
            }
        } catch (IOException x) {
            x.printStackTrace();
        }
    }

    private void readGames(File file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            int i = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                    items.put(InnerIds.getNextId(line, Helper.Keys.ItemId), new Item(line));
            }
        } catch (IOException x) {
            x.printStackTrace();
        }
    }

    private void readUsers(File file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            int i = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                    users.put(InnerIds.getNextId(line, Helper.Keys.UserId), new User(line));
            }
        } catch (IOException x) {
            x.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getInteraction() {
        return interaction;
    }

    public void setInteraction(String interaction) {
        this.interaction = interaction;
    }
}
