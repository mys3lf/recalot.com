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

package com.recalot.model.data.connections.downloader;

import com.recalot.common.UnZip;
import com.recalot.common.exceptions.NotFoundException;
import com.recalot.model.data.connections.base.DataSourceBase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public abstract class BaseDownloaderDataSource  extends DataSourceBase {

    public BaseDownloaderDataSource(){
        super();
    }


    protected File downloadData(String name, String urlString) throws IOException, NotFoundException {
        String userHome = System.getProperty("user.home");
        String separator = System.getProperty("file.separator");

        File recalotFolder = createFolder(userHome, "recalot");
        File dataFolder = createFolder(recalotFolder.getAbsolutePath(), name);

        if(dataFolder.listFiles().length == 0) {
            URL url = new URL(urlString);
            File file = new File(url.getFile());

            saveUrl(recalotFolder.getAbsolutePath() + separator + file.getName(), urlString);

            setInfo("Unzipping file");
            UnZip.unZipIt(recalotFolder.getAbsolutePath() + separator +  file.getName(), dataFolder.getAbsolutePath());
        }

        return dataFolder;
    }

    protected File createFolder(String path, String folder){
        File tempDir = new File(path, folder);
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }

        return tempDir;
    }

    private void saveUrl(final String filename, final String urlString)
            throws IOException, NotFoundException {
        InputStream in = null;
        FileOutputStream fout = null;
        try {

            java.net.HttpURLConnection con = (HttpURLConnection)new URL(urlString).openConnection();

            if (con.getResponseCode() != 200) {
                // error handle here!;
                throw new NotFoundException("The url %s can not be accessed!", urlString);
            }

            int chunkSize = 1024 * 64;
            // begin to download the file
            int file_size = con.getContentLength();

            in = con.getInputStream();

            fout = new FileOutputStream(filename);
            int downloaded = 0;
            final byte data[] = new byte[chunkSize];
            int count;
            while ((count = in.read(data, 0, chunkSize)) != -1) {
                fout.write(data, 0, count);

                downloaded+=count;
                setInfo(String.format("Downloaded %s bytes of a total %s bytes", downloaded, file_size));
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (fout != null) {
                fout.close();
            }
        }
    }

    @Override
    public void close() throws IOException {

    }


}
