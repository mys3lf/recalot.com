// Copyright (C) 2016 Matthäus Schmedding
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

package com.recalot.views.common;

import com.recalot.common.configuration.ConfigurationService;
import org.osgi.framework.BundleContext;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Closeable;
import java.util.Dictionary;

/**
 * Created by matthaeus.schmedding on 14.04.2015.
 */
public class WebService extends ConfigurationService implements Closeable {

    private final BundleContext context;
    private String lastPath;
    private HttpServiceTracker httpTracker;
    private HttpServlet servlet;
    public String PATH = "path";

    public WebService(String id, BundleContext context, HttpServlet servlet, Dictionary config) {
        setConfig(config);
        setPId(id);

        this.servlet = servlet;
        this.context = context;
    }

    @Override
    public void close() {
        this.httpTracker.close();
    }

    @Override
    public void onUpdate() {
        if (this.lastPath == null || !this.lastPath.equals(getConfig().get(getPId() + "." + PATH))) {
            this.httpTracker.unregister(this.lastPath);
            this.lastPath = (String) getConfig().get(getPId() + "." + PATH);
            this.httpTracker.registerServlet(lastPath, servlet, null, null);
        }
    }

    public void initialize() {
        this.httpTracker = new HttpServiceTracker(context);
        this.httpTracker.open();

        this.lastPath = getConfig().get(getPId() + "." + PATH) != null ? (String) getConfig().get(getPId() + "." + PATH) : "/";
        this.httpTracker.registerServlet(this.lastPath, servlet, null, null);
    }

    public static void processOptionsRequest(HttpServletRequest req, HttpServletResponse res, String allow){
        res.setHeader("Access-Control-Allow-Origin", "*");
        // "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS"
        res.setHeader("Allow", allow) ;
        res.setHeader("Access-Control-Allow-Methods", allow) ;
        res.setHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");
    }
}
