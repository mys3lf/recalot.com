package com.recalot.unittests;

import com.recalot.unittests.helper.WebRequest;
import com.recalot.unittests.helper.WebResponse;
import org.junit.Before;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by matthaeus.schmedding on 24.04.2015.
 */
public class TestsBase {

    public static String HOST = "http://localhost:8080/";
    public static String SourcesPath = "sources/";
    public static String SourceId = "mysql-test";
    public static String MLSourceId = "ml-test";
    public static String PathSeparator = "/";

    public static String JsonMimeType = "application/json; charset=UTF-8";

    public boolean isConnected = false;


    @Before
    public void checkIfSQLIsConnectedAndConnectIfNot() throws UnsupportedEncodingException {

        if (!isConnected) {
            try {

                WebResponse response = WebRequest.execute(HOST + SourcesPath + SourceId);
                if (response == null) {
                    initializeSQLSource();

                    Thread.sleep(2000);
                }

            } catch (Exception e) {
                initializeSQLSource();
            }


            try {

                WebResponse response = WebRequest.execute(HOST + SourcesPath + MLSourceId);
                if (response == null) {
                    initializeMovieLensSource();

                    Thread.sleep(10000);
                }

            } catch (Exception e) {
                initializeMovieLensSource();
            }

            isConnected = true;
        }
    }

    private void initializeSQLSource() throws UnsupportedEncodingException {
        Map<String, String> params = new Hashtable<>();
        params.put("data-builder-id", "mysql");
        params.put("source-id", "mysql-test");
        params.put("sql-server", "mysql://localhost:3306");
        params.put("sql-database", "recalot_test");
        params.put("sql-username", "root");
        params.put("sql-password", "mysqlpassword");

        WebResponse response = WebRequest.execute(WebRequest.HTTPMethod.PUT, HOST + SourcesPath, params);

        assertNotNull(response);
        assertEquals(response.getContentType(), JsonMimeType);
        assertNotNull(response.getBody());
        assertEquals(response.getResponseCode(), 200);
    }

    private void initializeMovieLensSource() throws UnsupportedEncodingException {
        Map<String, String> params = new Hashtable<>();
        params.put("source-id", "ml-test");
        params.put("data-builder-id", "ml");
        params.put("dir", "C:/Privat/3_Uni/5_Workspaces/data/ml-1m");

        WebResponse response = WebRequest.execute(WebRequest.HTTPMethod.PUT, HOST + SourcesPath, params);

        assertNotNull(response);
        assertEquals(response.getContentType(), JsonMimeType);
        assertNotNull(response.getBody());
        assertEquals(response.getResponseCode(), 200);
    }
}
