package com.recalot.model.data.connections.downloader;

import com.recalot.common.exceptions.BaseException;

import java.io.IOException;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class CiaoDataSource extends BaseDownloaderDataSource{

    public CiaoDataSource(){
        super();
    }

    @Override
    public void connect() throws BaseException {
        try {
            downloadData("ciao", "http://www.public.asu.edu/~jtang20/datasetcode/ciao_with_rating_timestamp_txt.zip");
            downloadData("epinions", "http://www.public.asu.edu/~jtang20/datasetcode/epinions_with_rating_timestamp_txt.zip");
            downloadData("filmtrust", "http://www.librec.net/datasets/filmtrust.zip");
            downloadData("jester_1_1", "http://eigentaste.berkeley.edu/dataset/jester_dataset_1_1.zip");
            downloadData("jester_1_2", "http://eigentaste.berkeley.edu/dataset/jester_dataset_1_2.zip");
            downloadData("jester_1_3", "http://eigentaste.berkeley.edu/dataset/jester_dataset_1_3.zip");
            downloadData("jester_2", "http://eigentaste.berkeley.edu/dataset/jester_dataset_2.zip");
            downloadData("jester_3", "http://eigentaste.berkeley.edu/dataset/jester_dataset_3.zip");
            downloadData("book-crossing", "http://www2.informatik.uni-freiburg.de/~cziegler/BX/BX-CSV-Dump.zip");
            downloadData("ml-latestSmall", "http://files.grouplens.org/datasets/movielens/ml-latest-small.zip");
            downloadData("ml-latest", "http://files.grouplens.org/datasets/movielens/ml-latest.zip");



        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
