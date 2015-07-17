package com.recalot.unittests;

import com.recalot.common.Helper;
import com.recalot.common.communication.RecommendationResult;
import com.recalot.common.interfaces.model.rec.RecommenderInformation;
import com.recalot.unittests.helper.WebRequest;
import com.recalot.unittests.helper.WebResponse;
import flexjson.JSONDeserializer;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by matthaeus.schmedding on 24.04.2015.
 */
public class RecommenderTests extends TestsBase {
    private static  String Path = "rec/";
    private static String TrainPath = "train/";


    public static HashMap sendTrain(String id, Map<String, String> params) throws UnsupportedEncodingException {
        WebResponse response = WebRequest.execute(WebRequest.HTTPMethod.PUT, HOST + TrainPath, params);
        assertNotNull(response);
        assertEquals(response.getContentType(), JsonMimeType);
        assertNotNull(response.getBody());
        assertEquals(response.getResponseCode(), 200);
        HashMap info = new JSONDeserializer<HashMap>().deserialize(response.getBody());

        assertNotNull(info);
        assertNotEquals(info.get("state"), RecommenderInformation.RecommenderState.AVAILABLE.toString());
        assertEquals(info.get("id"), id);

        return info;
    }

    public static HashMap getRecommendations(String id, Map<String, String> params) {

        WebResponse response = WebRequest.execute(HOST + Path + id);
        assertNotNull(response);
        assertEquals(response.getContentType(), JsonMimeType);
        assertNotNull(response.getBody());
        assertEquals(response.getResponseCode(), 200);

        HashMap rec = new JSONDeserializer<HashMap>().deserialize(response.getBody());

        assertNotNull(rec);
        assertEquals(rec.get("recommender"), id);
        assertNotNull(rec.get("items"));
        assertNotEquals(((ArrayList) rec.get("items")).size(), 0);

        return rec;
    }

    private HashMap delete(String id) {
        WebResponse response = WebRequest.execute(WebRequest.HTTPMethod.DELETE, HOST + TrainPath + id);
        assertNotNull(response);
        assertEquals(response.getContentType(), JsonMimeType);
        assertNotNull(response.getBody());
        assertEquals(response.getResponseCode(), 200);

        HashMap del = new JSONDeserializer<HashMap>().deserialize(response.getBody());

        assertNotNull(del);

        WebResponse response2 = WebRequest.execute( HOST + TrainPath + id);
        assertNull(response2);

        return del;
    }

    @Test
    public void getRecommenderStatus() {
        WebResponse response = WebRequest.execute(HOST + TrainPath);
        assertNotNull(response);
        assertEquals(response.getContentType(), JsonMimeType);
        assertNotNull(response.getBody());
        assertEquals(response.getResponseCode(), 200);
        List<HashMap> recs = new JSONDeserializer<List<HashMap>>().deserialize(response.getBody());
        assertNotNull(recs);
        assertNotEquals(recs.size(), 0);

        String id = null;
        for(int i = 0; i < recs.size(); i++){
            if(recs.get(i).get("state").equals("AVAILABLE")){
                id = (String)recs.get(i).get("id");
                break;
            }
        }
        WebResponse response2 = WebRequest.execute(HOST + TrainPath + id);
        assertNotNull(response2);
        assertEquals(response2.getContentType(), JsonMimeType);
        assertNotNull(response2.getBody());
        assertEquals(response2.getResponseCode(), 200);

        HashMap source = new JSONDeserializer<HashMap>().deserialize(response2.getBody());
        assertNotNull(source);
        assertNotNull(source.get("id"));
        assertNotNull(source.get("key"));
        assertNotNull(source.get("state"));
        assertNotNull(source.get("configuration"));
    }

    @Test
    public void testMostPopularRecommender() {
        String id = UUID.randomUUID().toString();
        //String id = "mostpopular";
        Map<String, String> params = new Hashtable<>();
        params.put(Helper.Keys.SourceId, MLSourceId);
        params.put(Helper.Keys.RecommenderBuilderId, "mp");
        params.put(Helper.Keys.ID, id);
        params.put("topN", "10");

        try {
            HashMap train = sendTrain(id, params);
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HashMap rec = getRecommendations(id, params);

        HashMap del = delete(id);
    }

    @Test
    public void testCosineKNNRecommender() {
        String id = UUID.randomUUID().toString();
        Map<String, String> params = new Hashtable<>();
        params.put(Helper.Keys.SourceId, MLSourceId);
        params.put(Helper.Keys.RecommenderBuilderId, "cosine-user-knn");
        params.put(Helper.Keys.ID, id);

        try {
            HashMap train = sendTrain(id, params);
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HashMap rec = getRecommendations(id, params);

     //   HashMap del = delete(id);
    }

    @Test
    public void testBPRMFRecommender() {
        String id = UUID.randomUUID().toString();
        Map<String, String> params = new Hashtable<>();
        params.put(Helper.Keys.SourceId, MLSourceId);
        params.put(Helper.Keys.RecommenderBuilderId, "bprmf");
        params.put(Helper.Keys.ID, id);

        try {
            HashMap train = sendTrain(id, params);
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HashMap rec = getRecommendations(id, params);

     //   HashMap del = delete(id);
    }

    @Test
    public void testFunkSVDRecommender() {
        String id = UUID.randomUUID().toString();
        Map<String, String> params = new Hashtable<>();
        params.put(Helper.Keys.SourceId, MLSourceId);
        params.put(Helper.Keys.RecommenderBuilderId, "funk-svd");
        params.put(Helper.Keys.ID, id);

        try {
            HashMap train = sendTrain(id, params);
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HashMap rec = getRecommendations(id, params);

     //   HashMap del = delete(id);
    }
    //TODO: fill with algorithms
}
