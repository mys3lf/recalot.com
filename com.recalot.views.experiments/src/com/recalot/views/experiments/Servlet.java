package com.recalot.views.experiments;

import com.recalot.common.Helper;
import com.recalot.common.communication.TemplateResult;
import com.recalot.common.interfaces.controller.ExperimentsController;
import com.recalot.views.common.GenericControllerHandler;
import com.recalot.views.common.HTTPMethods;
import com.recalot.views.common.WebService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Matthaeus.schmedding
 */
public class Servlet extends HttpServlet {
    private GenericControllerHandler handler;

    public Servlet(GenericControllerHandler handler) {
        this.handler = handler;
    }

    /**
     * TODO: deactivate it later. Just for debugging
     *
     * @param req
     * @param res
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res, HTTPMethods.GET);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res, HTTPMethods.POST);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res, HTTPMethods.DELETE);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res, HTTPMethods.PUT);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        WebService.processOptionsRequest(req, res, "GET, POST, PUT, DELETE, OPTIONS");
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse res, HTTPMethods method) throws ServletException, IOException {
        res.setHeader("Access-Control-Allow-Origin", "*");
        String pathInfo = req.getPathInfo();

        //Collect all parameter
        Map<String, String> params = new HashMap<>();

        Map names = req.getParameterMap();
        if (names != null) {
            for (Object key : names.keySet()) {
                params.put(URLDecoder.decode((String) key, "UTF-8"), URLDecoder.decode(req.getParameter((String) key), "UTF-8"));
            }
        }

        //default template is text
        String templateKey = "json";

        if (params.containsKey(Helper.Keys.OutputParam)) {
            String tempKey = params.get(Helper.Keys.OutputParam);
            if (tempKey != null && !tempKey.equals("")) {
                templateKey = tempKey;
            }
        }

        TemplateResult result = null;
        try {
            if (pathInfo != null && !pathInfo.equals("") && !pathInfo.equals("/")) {

                pathInfo = pathInfo.substring(1);
                String[] split = pathInfo.split("/");

                switch (split.length) {
                    case 1: {
                        if (split[0].toLowerCase().equals("metrics") && method == HTTPMethods.GET) {
                            result = this.handler.process(ExperimentsController.ExperimentsRequestAction.GetMetrics, templateKey);
                        } else if (split[0].toLowerCase().equals("splitters") && method == HTTPMethods.GET) {
                            result = this.handler.process(ExperimentsController.ExperimentsRequestAction.GetSplitters, templateKey);
                        } else  {
                            switch (method){
                                case GET: {
                                    params.put(Helper.Keys.ExperimentId, split[0]);
                                    result = this.handler.process(ExperimentsController.ExperimentsRequestAction.GetExperiment, templateKey, params);
                                    break;
                                }
                                case DELETE: {
                                    params.put(Helper.Keys.ExperimentId, split[0]);
                                    result = this.handler.process(ExperimentsController.ExperimentsRequestAction.DeleteExperiment, templateKey, params);
                                    break;
                                }
                            }
                        }
                    }
                    break;
                    case 2: {
                        String id = split[1];
                        if (split[0].toLowerCase().endsWith("metrics") && method == HTTPMethods.GET) {
                            params.put(Helper.Keys.MetricIDs, id);
                            result = this.handler.process(ExperimentsController.ExperimentsRequestAction.GetMetric, templateKey, params);
                        } else if (split[0].toLowerCase().endsWith("splitters") && method == HTTPMethods.GET) {
                            params.put(Helper.Keys.DataSplitterId, id);
                            result = this.handler.process(ExperimentsController.ExperimentsRequestAction.GetSplitter, templateKey, params);
                        }
                    }
                    break;
                }
            } else {
                switch (method) {
                    case GET: {
                        result = handler.process(ExperimentsController.ExperimentsRequestAction.GetExperiments, templateKey, params);
                        break;
                    }
                    case PUT: {
                        result = this.handler.process(ExperimentsController.ExperimentsRequestAction.CreateExperiment, templateKey, params);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == null) {


            //   out.println("TODO print error");
        } else {
            res.setContentType(result.getContentType());
            res.setCharacterEncoding(result.getCharset().name());
            res.setStatus(result.getStatus());
            PrintWriter out = res.getWriter();
            Helper.copy(out, result.getResult());
        }
    }
}