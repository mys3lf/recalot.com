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

package com.recalot.views.data.tracking;

import com.recalot.common.Helper;
import com.recalot.common.communication.TemplateResult;
import com.recalot.common.interfaces.controller.DataAccessController;
import com.recalot.views.common.GenericControllerHandler;
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
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        WebService.processOptionsRequest(req, res, "POST, OPTIONS");
    }
    protected void processRequest(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setHeader("Access-Control-Allow-Origin", "*");
        String pathInfo = req.getPathInfo();
        TemplateResult result = null;

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


        if (pathInfo != null && !pathInfo.equals("") && pathInfo.length() > 1) {
            String[] split = pathInfo.substring(1).split("\\/");

            if (split.length >= 6) {

                if (split[0].toLowerCase().equals("sources") && split[2].toLowerCase().equals("users") && split[4].toLowerCase().equals("items")) {
                    String sourceId = split[1];
                    String userId = split[3];
                    String itemId =split[5];

                    if (userId != null && !userId.trim().equals("")
                            && itemId != null && !itemId.trim().equals("")
                            && templateKey != null && !templateKey.trim().equals("")
                            ) {

                        params.put(Helper.Keys.SourceId, sourceId);
                        params.put(Helper.Keys.ItemId, itemId);
                        params.put(Helper.Keys.UserId, userId);

                        result = handler.process(DataAccessController.DataAccessRequestAction.AddInteraction, templateKey, params);
                    }
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