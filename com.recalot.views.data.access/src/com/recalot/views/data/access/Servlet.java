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

package com.recalot.views.data.access;

import com.recalot.common.Helper;
import com.recalot.common.communication.TemplateResult;
import com.recalot.common.interfaces.controller.DataAccessController;
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res, HTTPMethods.GET);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res, HTTPMethods.POST);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res, HTTPMethods.PUT);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res, HTTPMethods.DELETE);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
          WebService.processOptionsRequest(req, res, "GET, POST, PUT, DELETE, OPTIONS");
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse res, HTTPMethods method) throws ServletException, IOException {
        res.setHeader("Access-Control-Allow-Origin", "*");

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


        String pathInfo = req.getPathInfo();
        TemplateResult result = null;

        if (pathInfo != null && !pathInfo.equals("")) {
            pathInfo = pathInfo.substring(1);
            String[] split = pathInfo.split("/");

            switch (split.length) {
                case 2: {
                    if (split[0].toLowerCase().equals("sources")) {
                        params.put(Helper.Keys.SourceId, split[1]);

                        switch (method) {
                            case GET:
                                result = handler.process(DataAccessController.DataAccessRequestAction.GetData, templateKey, params);
                        }
                    }

                    break;
                }
                case 3: {
                    if (split[0].toLowerCase().equals("sources")) {

                        params.put(Helper.Keys.SourceId, split[1]);

                        if (split[2].toLowerCase().equals("users")) {
                            switch (method) {
                                case PUT:
                                    result = handler.process(DataAccessController.DataAccessRequestAction.CreateUser, templateKey, params);
                                    break;
                                case GET:
                                    result = handler.process(DataAccessController.DataAccessRequestAction.GetUsers, templateKey, params);
                                    break;
                            }
                        } else if (split[2].toLowerCase().equals("items")) {
                            switch (method) {
                                case PUT:
                                    result = handler.process(DataAccessController.DataAccessRequestAction.CreateItem, templateKey, params);
                                    break;
                                case GET:
                                    result = handler.process(DataAccessController.DataAccessRequestAction.GetItems, templateKey, params);
                                    break;
                            }
                        } else if (split[2].toLowerCase().equals("interactions")) {
                            switch (method) {
                                case GET:
                                    result = handler.process(DataAccessController.DataAccessRequestAction.GetInteractions, templateKey, params);
                                    break;
                            }
                        } else if (split[2].toLowerCase().equals("relations")) {
                            switch (method) {
                                case GET:
                                    result = handler.process(DataAccessController.DataAccessRequestAction.GetRelations, templateKey, params);
                                    break;
                                case PUT:
                                    result = handler.process(DataAccessController.DataAccessRequestAction.CreateRelation, templateKey, params);
                                    break;
                            }
                        }
                    }
                    break;
                }
                case 4: {
                    if (split[0].toLowerCase().equals("sources")) {

                        params.put(Helper.Keys.SourceId, split[1]);

                        if (split[2].toLowerCase().equals("users")) {

                            params.put(Helper.Keys.UserId, split[3]);

                            switch (method) {
                                case PUT:
                                    result = handler.process(DataAccessController.DataAccessRequestAction.UpdateUser, templateKey, params);
                                    break;
                                case GET:
                                    result = handler.process(DataAccessController.DataAccessRequestAction.GetUser, templateKey, params);
                                    break;
                            }
                        } else if (split[2].toLowerCase().equals("items")) {

                            params.put(Helper.Keys.ItemId, split[3]);

                            switch (method) {
                                case PUT:
                                    result = handler.process(DataAccessController.DataAccessRequestAction.UpdateItem, templateKey, params);
                                    break;
                                case GET:
                                    result = handler.process(DataAccessController.DataAccessRequestAction.GetItem, templateKey, params);
                                    break;
                            }
                        } else if (split[2].toLowerCase().equals("relations")) {

                            params.put(Helper.Keys.RelationId, split[3]);

                            switch (method) {
                                case PUT:
                                    result = handler.process(DataAccessController.DataAccessRequestAction.UpdateRelation, templateKey, params);
                                    break;
                                case GET:
                                    result = handler.process(DataAccessController.DataAccessRequestAction.GetRelation, templateKey, params);
                                    break;
                            }
                        }
                    }
                    break;
                }
                case 5: {
                    if (split[0].toLowerCase().equals("sources") && split[2].toLowerCase().equals("users") && split[4].toLowerCase().equals("interactions")) {

                        params.put(Helper.Keys.SourceId, split[1]);
                        params.put(Helper.Keys.UserId, split[3]);

                        switch (method) {
                            case GET:
                                result = handler.process(DataAccessController.DataAccessRequestAction.GetInteractions, templateKey, params);
                                break;
                        }
                    } else if (split[0].toLowerCase().equals("sources") && split[2].toLowerCase().equals("users") && split[4].toLowerCase().equals("relations")) {

                        params.put(Helper.Keys.SourceId, split[1]);
                        params.put(Helper.Keys.FromId, split[3]);

                        switch (method) {
                            case GET:
                                result = handler.process(DataAccessController.DataAccessRequestAction.GetRelations, templateKey, params);
                                break;
                        }
                    }  else if (split[0].toLowerCase().equals("sources") && split[2].toLowerCase().equals("items") && split[4].toLowerCase().equals("interactions")) {

                        params.put(Helper.Keys.SourceId, split[1]);
                        params.put(Helper.Keys.ItemId, split[3]);

                        switch (method) {
                            case GET:
                                result = handler.process(DataAccessController.DataAccessRequestAction.GetInteractions, templateKey, params);
                                break;
                        }
                    }

                    break;
                }
                case 6: {
                     if (split[0].toLowerCase().equals("sources") && split[2].toLowerCase().equals("users") && split[4].toLowerCase().equals("relations")) {

                        params.put(Helper.Keys.SourceId, split[1]);
                        params.put(Helper.Keys.FromId, split[3]);
                        params.put(Helper.Keys.ToId, split[5]);

                        switch (method) {
                            case GET:
                                result = handler.process(DataAccessController.DataAccessRequestAction.GetRelations, templateKey, params);
                                break;
                        }
                    }

                    break;
                }
                case 7: {
                    if (split[0].toLowerCase().equals("sources") && split[2].toLowerCase().equals("users") && split[4].toLowerCase().equals("items") && split[6].toLowerCase().equals("interactions")) {

                        params.put(Helper.Keys.SourceId, split[1]);
                        params.put(Helper.Keys.UserId, split[3]);
                        params.put(Helper.Keys.ItemId, split[5]);

                        switch (method) {
                            case GET:
                                result = handler.process(DataAccessController.DataAccessRequestAction.GetInteraction, templateKey, params);
                                break;
                        }
                    }

                    break;
                }
            }
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