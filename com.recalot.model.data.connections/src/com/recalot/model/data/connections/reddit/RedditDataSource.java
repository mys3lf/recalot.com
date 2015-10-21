package com.recalot.model.data.connections.reddit;

import com.recalot.common.communication.Item;
import com.recalot.common.communication.User;
import com.recalot.common.exceptions.BaseException;
import com.recalot.model.data.connections.base.DataSourceBase;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 *
 * Created by matthaeus.schmedding on 11.06.2015.
 */
public class RedditDataSource extends DataSourceBase {
    private File dir;
    private float interactionId = 0f;
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
        }
    }

    private void readFile(String userName, File file) {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line = null;


            while ((line = reader.readLine()) != null) {
                int i = 0;
                String[] split = line.split(";");

                if (split.length == 2) {

                    Date date = DatatypeConverter.parseDateTime(split[0]).getTime();

                    List<String> words = splitIntoWords(split[1]);

                    for (String word : words) {
                        addNewItem(word);
                        addWordInteraction(userName, word, (interactionId++) + "", date, i++);
                    }
                }
            }
        } catch (IOException x) {
            x.printStackTrace();
        }
    }

    private void addWordInteraction(String user, String word, String id, Date date, int i) {
        interactions.put(id, new com.recalot.common.communication.Interaction(id, user, word, new Date(date.getTime() + i), "view", "1", new HashMap<>()));
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
                    words.add(last.trim());
                    last = null;
                } else if ((!concat && !word.equals("'") && last != null)) {  //punctuation? add the last word and set punctuation as last word
                    words.add(last.trim());
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
            words.add(last.trim());
        }

        return words;
    }

    private void addNewItem(String word) {
        word = word.toLowerCase();
        if (!items.containsKey(word)) {
            items.put(word, new Item(word));
        }
    }

    private String createNewUser(String name) {
        String userName = name.substring(0, name.length() - 4);
        if (!users.containsKey(userName)) {
            users.put(userName, new User(userName));
        }
        return userName;
    }

    @Override
    public void close() throws IOException {

    }

    public void setDir(String file) {
        this.dir = new File(file);
    }
}
