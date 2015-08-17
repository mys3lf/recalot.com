package com.recalot.views.recommend;

import com.recalot.common.Helper;
import com.recalot.common.communication.TemplateResult;
import com.recalot.common.interfaces.controller.RecommenderController;
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
     * @throws ServletException
     * @throws IOException
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

        //default template is json
        String templateKey = "json";

        if (params.containsKey(Helper.Keys.OutputParam)) {
            String tempKey = params.get(Helper.Keys.OutputParam);
            if (tempKey != null && !tempKey.equals("")) {
                templateKey = tempKey;
            }
        }


        TemplateResult result = null;
        try {
            if (pathInfo != null && !pathInfo.equals("")) {
                //request: /{recommender-id}
                pathInfo = pathInfo.substring(1);
                String[] split = pathInfo.split("/");

                if(split.length == 1){
                   params.put(Helper.Keys.RecommenderId, split[0]);

                   result = handler.process(RecommenderController.RecommenderRequestAction.Recommend, templateKey, params);
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