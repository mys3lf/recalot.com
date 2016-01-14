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
package com.recalot.demos.shoeandu.controller;

import com.recalot.common.communication.Item;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.data.DataSource;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class LuceneSearch {
    private ArrayList<String> ignoreList;
    private RAMDirectory index;

    public LuceneSearch(){
        ignoreList = new ArrayList<>();
        ignoreList.add("src");
        ignoreList.add("url");
        ignoreList.add("timeStamp");
        ignoreList.add("content");
    }

    public void initialize(DataSource source) {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        index = new RAMDirectory();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        IndexWriter w = null;
        try {
            w = new IndexWriter(index, config);

            for (Item item: source.getItems()) {
                addDoc(w, item);
            }

            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BaseException e) {
            e.printStackTrace();
        }
    }

    private void addDoc(IndexWriter w, Item item) throws IOException {
        Document doc = new Document();

        for (String key : item.getContent().keySet()) {
            if (ignoreList.contains(key)) {
                continue;
            }

            if (key.equals("sizes") || key.equals("tags")) {
                String[] split = item.getValue(key).split(",");
                if(split != null) {
                    for(String s : split){
                        doc.add(new StringField(key, s, Field.Store.YES));
                    }
                }
            } else if(key.equals("title") || key.equals("description")){
                doc.add(new TextField(key, item.getValue(key), Field.Store.YES));
            } else {
                doc.add(new StringField(key, item.getValue(key), Field.Store.YES));
            }
        }

        w.addDocument(doc);
    }
}
