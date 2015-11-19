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

package com.recalot.model.data.connections.reddit;

import com.recalot.common.communication.Interaction;
import com.recalot.common.communication.Item;
import com.recalot.common.communication.User;
import com.recalot.common.exceptions.BaseException;
import com.recalot.model.data.connections.base.DataSourceBase;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.util.*;

/**
 * Created by matthaeus.schmedding on 11.06.2015.
 */
public class RedditDataSource extends DataSourceBase {
    private File dir;
    private float interactionId = 0f;
    private boolean initialized = false;
    private List<Interaction> sortedInteractions = null;

    public RedditDataSource() {
        super();
    }

    @Override
    public void connect() throws BaseException {
        if (this.dir != null && this.dir.exists()) {

            for (File userFile : dir.listFiles()) {
                if (userFile.getName().endsWith(".csv")) {

                    String userName = createNewUser(userFile.getName());

                    readFile(userName, userFile);
                }
            }

            initialized = true;
        }
    }

    private void readFile(String userName, File file) {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line = null;


            while ((line = reader.readLine()) != null) {
                int i = 0;
                String[] split = line.split("00;");

                if (split.length == 2) {

                    Date date = DatatypeConverter.parseDateTime(split[0] + "00").getTime();

                    List<String> words = splitIntoWords(split[1]);

                    for (String word : words) {
                        addNewItem(word.intern());
                        addWordInteraction(userName.intern(), word.intern(), (interactionId++) + "", date, i++);
                    }
                }
            }
        } catch (IOException x) {
            x.printStackTrace();
        }
    }

    private void addWordInteraction(String user, String word, String id, Date date, int i) {
        interactions.put(id, new com.recalot.common.communication.Interaction(id, user, word, new Date(date.getTime() + i), "view".intern(), "1".intern(), new HashMap<>()));
    }

    private List<String> splitIntoWords(String sentence) {
        List<String> words = new ArrayList<>();
        sentence = StringEscapeUtils.unescapeHtml4(sentence);
        String[] parts = sentence.split("(?<!^)\\b");

        String last = null;
        boolean concat = false;

        for (String word : parts) {
            if (last != null) {
                if (word.trim().length() == 0) { //spaces? add the last word
                    words.add(last.trim().toLowerCase().intern());
                    last = null;
                } else if ((!concat && !word.equals("'") && last != null)) {  //punctuation? add the last word and set punctuation as last word
                    words.add(last.trim().toLowerCase().intern());
                    last = word;
                } else if (word.equals("'")) { //quotes? concat the following words
                    concat = true;
                    last += word;
                } else {
                    if (concat) {
                        last += word;
                    } else {
                        last = word;
                    }
                    concat = false;
                }
            } else {
                last = word;
            }
        }

        if (last != null) { //a word in pipeline
            words.add(last.trim().intern());
        }

        return words;
    }

    private void addNewItem(String word) {
        word = word.intern();
        if (!items.containsKey(word)) {
            items.put(word, new Item(word));
        }
    }

    private String createNewUser(String name) {
        String userName = name.substring(0, name.length() - 4).intern();
        if (!users.containsKey(userName)) {
            users.put(userName, new User(userName));
        }
        return userName;
    }

    @Override
    public void close() throws IOException {

    }


    @Override
    public Interaction[] getInteractions() throws BaseException {
        if (initialized) {
            if (sortedInteractions == null) {
                sortedInteractions = new ArrayList<>(interactions.values());
                Collections.sort(sortedInteractions, (a2, a1) -> a2.getTimeStamp().compareTo(a1.getTimeStamp()));
            }

            return sortedInteractions.toArray(new Interaction[sortedInteractions.size()]);
        } else {
            return new Interaction[0];
        }
    }


    public void setDir(String file) {
        this.dir = new File(file);
    }
}
