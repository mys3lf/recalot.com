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

package com.recalot.views.recommend.train;

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
                //request: /{recommender-id}?source-id=xy&id=recommender-instance-id&args...
                pathInfo = pathInfo.substring(1);
                String[] split = pathInfo.split("/");

                if (split.length == 1) {

                    params.put(Helper.Keys.RecommenderId, split[0]);

                    switch (method) {
                        case GET: {
                            if (split[0].startsWith(Helper.Keys.RecommenderBuilderIdPrefix)) {

                                params.put(Helper.Keys.RecommenderId, split[0].substring(Helper.Keys.RecommenderBuilderIdPrefix.length()));
                                result = handler.process(RecommenderController.RecommenderRequestAction.GetRecommenderBuilder, templateKey, params);
                            } else {
                                result = handler.process(RecommenderController.RecommenderRequestAction.GetRecommender, templateKey, params);
                            }
                            break;
                        }
                        case PUT: {
                            result = handler.process(RecommenderController.RecommenderRequestAction.UpdateRecommender, templateKey, params);
                            break;
                        }
                        case DELETE: {
                            result = handler.process(RecommenderController.RecommenderRequestAction.DeleteRecommender, templateKey, params);
                            break;
                        }
                    }
                }
                if (split.length == 2 && split[0].toLowerCase().equals("sources")) {
                    params.put(Helper.Keys.SourceId, split[1]);

                    switch (method) {
                        case PUT: {
                            result = handler.process(RecommenderController.RecommenderRequestAction.CreateRecommender, templateKey, params);
                            break;
                        }
                    }
                }
            } else {
                switch (method) {
                    case GET: {
                        result = handler.process(RecommenderController.RecommenderRequestAction.GetRecommenders, templateKey, params);

                        break;
                    }
                    case PUT: {
                        result = handler.process(RecommenderController.RecommenderRequestAction.CreateRecommender, templateKey, params);

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