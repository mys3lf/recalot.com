package com.recalot.unittests;

import com.recalot.common.Helper;
import com.recalot.common.impl.experiment.Experiment;
import com.recalot.common.interfaces.model.experiment.DataSplitter;
import com.recalot.common.interfaces.model.experiment.Metric;
import com.recalot.common.interfaces.model.rec.RecommenderInformation;
import com.recalot.unittests.helper.WebRequest;
import com.recalot.unittests.helper.WebResponse;
import flexjson.JSONDeserializer;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by matthaeus.schmedding on 24.04.2015.
 */
public class ExperimentsTests extends TestsBase {
    private String Path = "experiments/";
    private String MetricsPath = "metrics/";
    private String SplitterPath = "splitters/";

    @Test
    public void getMetrics() {

        WebResponse response = WebRequest.execute(HOST + Path + MetricsPath);
        assertNotNull(response);
        assertEquals(response.getContentType(), JsonMimeType);
        assertNotNull(response.getBody());
        assertEquals(response.getResponseCode(), 200);
        List<HashMap> metrics = new JSONDeserializer<List<HashMap>>().deserialize(response.getBody());
        assertNotNull(metrics);
        assertNotEquals(metrics.size(), 0);

        WebResponse response2 = WebRequest.execute(HOST + Path + MetricsPath + metrics.get(0).get("id"));
        assertNotNull(response2);
        assertEquals(response2.getContentType(), JsonMimeType);
        assertNotNull(response2.getBody());
        assertEquals(response2.getResponseCode(), 200);
        HashMap metric = new JSONDeserializer<HashMap>().deserialize(response2.getBody());
        assertNotNull(metric);
        assertNotNull(metric.get("configuration"));
    }

    @Test
    public void getDataSplitter() {
        WebResponse response = WebRequest.execute(HOST + Path + SplitterPath);
        assertNotNull(response);
        assertEquals(response.getContentType(), JsonMimeType);
        assertNotNull(response.getBody());
        assertEquals(response.getResponseCode(), 200);
        List<HashMap> splitter = new JSONDeserializer<List<HashMap>>().deserialize(response.getBody());
        assertNotNull(splitter);
        assertNotEquals(splitter.size(), 0);
    }

    @Test
    public void getRandomSplitter() {
        String id = "random";
        WebResponse response = WebRequest.execute(HOST + Path + SplitterPath + id);
        assertNotNull(response);
        assertEquals(response.getContentType(), JsonMimeType);
        assertNotNull(response.getBody());
        assertEquals(response.getResponseCode(), 200);
        HashMap splitter = new JSONDeserializer<HashMap>().deserialize(response.getBody());
        assertNotNull(splitter);
        assertEquals(splitter.get("key"), id);
        assertNotNull(splitter.get("configuration"));
    }

    @Test
    public void getExperiments() {
        WebResponse response = WebRequest.execute(HOST + Path);
        assertNotNull(response);
        assertEquals(response.getContentType(), JsonMimeType);
        assertNotNull(response.getBody());
        assertEquals(response.getResponseCode(), 200);
        List<Experiment> experiments = new JSONDeserializer<List<Experiment>>().deserialize(response.getBody());
        assertNotNull(experiments);
        assertNotEquals(experiments.size(), 0);
    }

    @Test
    public void createMPExperiment() throws UnsupportedEncodingException {
        Map<String, String> params = new Hashtable<>();

        params.put(Helper.Keys.SourceId, SourceId);
        params.put(Helper.Keys.RecommenderId, "mp@mp-10,mp@mp-20");
        params.put(Helper.Keys.DataSplitterId, "random");
        params.put(Helper.Keys.MetricIDs, "precision@p10,recall@r10,precision@p5,recall@r5");
        params.put("p10.topN", "10");
        params.put("r10.topN", "10");
        params.put("p5.topN", "5");
        params.put("r5.topN", "5");
        params.put("mp-10.topN", "10");
        params.put("mp-20.topN", "20");
        params.put("random.nbFolds", "2");

        WebResponse response = WebRequest.execute(WebRequest.HTTPMethod.PUT, HOST + Path, params);
        assertNotNull(response);
        assertEquals(response.getContentType(), JsonMimeType);
        assertNotNull(response.getBody());
        assertEquals(response.getResponseCode(), 200);
        HashMap experiments = new JSONDeserializer<HashMap>().deserialize(response.getBody());
        assertNotNull(experiments);
        assertNotEquals(experiments.get("state"), "" + com.recalot.common.interfaces.model.experiment.Experiment.ExperimentState.FINISHED);
    }

    @Test
    public void create_MP_BPRMF_Experiment() throws UnsupportedEncodingException {
        Map<String, String> params = new Hashtable<>();

        params.put(Helper.Keys.SourceId, MLSourceId);
        params.put(Helper.Keys.RecommenderId, "mp@mp-10,bprmf");
        params.put(Helper.Keys.DataSplitterId, "random");
        params.put(Helper.Keys.MetricIDs, "precision@p10,recall@r10,precision@p5,recall@r5");
        params.put("p10.topN", "10");
        params.put("r10.topN", "10");
        params.put("p5.topN", "5");
        params.put("r5.topN", "5");
        params.put("mp-10.topN", "10");
        params.put("mp-20.topN", "20");
        params.put("random.nbFolds", "2");

        WebResponse response = WebRequest.execute(WebRequest.HTTPMethod.PUT, HOST + Path, params);
        assertNotNull(response);
        assertEquals(response.getContentType(), JsonMimeType);
        assertNotNull(response.getBody());
        assertEquals(response.getResponseCode(), 200);
        HashMap experiments = new JSONDeserializer<HashMap>().deserialize(response.getBody());
        assertNotNull(experiments);
        assertNotEquals(experiments.get("state"), "" + com.recalot.common.interfaces.model.experiment.Experiment.ExperimentState.FINISHED);
    }

    @Test
    public void create_MP_SVD_Experiment() throws UnsupportedEncodingException {
        Map<String, String> params = new Hashtable<>();

        params.put(Helper.Keys.SourceId, MLSourceId);
        params.put(Helper.Keys.RecommenderId, "mp@mp-10,funk-svd");
        params.put(Helper.Keys.DataSplitterId, "random");
        params.put(Helper.Keys.MetricIDs, "precision@p10,recall@r10,precision@p5,recall@r5");
        params.put("p10.topN", "10");
        params.put("r10.topN", "10");
        params.put("p5.topN", "5");
        params.put("r5.topN", "5");
        params.put("mp-10.topN", "10");
        params.put("mp-20.topN", "20");
        params.put("random.nbFolds", "2");

        WebResponse response = WebRequest.execute(WebRequest.HTTPMethod.PUT, HOST + Path, params);
        assertNotNull(response);
        assertEquals(response.getContentType(), JsonMimeType);
        assertNotNull(response.getBody());
        assertEquals(response.getResponseCode(), 200);
        HashMap experiments = new JSONDeserializer<HashMap>().deserialize(response.getBody());
        assertNotNull(experiments);
        assertNotEquals(experiments.get("state"), "" + com.recalot.common.interfaces.model.experiment.Experiment.ExperimentState.FINISHED);
    }

    @Test
    public void create_BPR_SVD_Experiment() throws UnsupportedEncodingException {
        Map<String, String> params = new Hashtable<>();

        params.put(Helper.Keys.SourceId, MLSourceId);
        params.put(Helper.Keys.RecommenderId, "bprmf,funk-svd");
        params.put(Helper.Keys.DataSplitterId, "random");
        params.put(Helper.Keys.MetricIDs, "precision@p10,recall@r10,precision@p5,recall@r5");
        params.put("p10.topN", "10");
        params.put("r10.topN", "10");
        params.put("p5.topN", "5");
        params.put("r5.topN", "5");
        params.put("mp-10.topN", "10");
        params.put("mp-20.topN", "20");
        params.put("random.nbFolds", "2");

        WebResponse response = WebRequest.execute(WebRequest.HTTPMethod.PUT, HOST + Path, params);
        assertNotNull(response);
        assertEquals(response.getContentType(), JsonMimeType);
        assertNotNull(response.getBody());
        assertEquals(response.getResponseCode(), 200);
        HashMap experiments = new JSONDeserializer<HashMap>().deserialize(response.getBody());
        assertNotNull(experiments);
        assertNotEquals(experiments.get("state"), "" + com.recalot.common.interfaces.model.experiment.Experiment.ExperimentState.FINISHED);
    }
}
