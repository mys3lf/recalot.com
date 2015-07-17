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
    }
}
