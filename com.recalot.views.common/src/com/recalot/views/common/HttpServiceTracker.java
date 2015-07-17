package com.recalot.views.common;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;

import javax.servlet.Servlet;
import java.util.*;

/**
 * @author Matthaeus.schmedding
 */
public class HttpServiceTracker extends org.osgi.util.tracker.ServiceTracker {
    private Map<String, Map> servletsAndResources;
    private HttpService httpService;

    public String Servlet = "servlet".intern();
    public String Dictionary = "dictionary".intern();
    public String HttpContext = "httpContext".intern();
    public String FilePath = "filePath".intern();

    public HttpServiceTracker(BundleContext context) {
        super(context, HttpService.class.getName(), null);
        servletsAndResources = new Hashtable<>();
    }


    @Override
    public void removedService(ServiceReference reference, Object service) {
        unregisterQueue();

        this.httpService = null;
    }

    @Override
    public Object addingService(ServiceReference reference) {
        // HTTP service is available, register our servlet...

        this.httpService = (HttpService) this.context.getService(reference);
        registerQueue();

        return httpService;
    }

    private void registerQueue() {
        if (servletsAndResources != null && httpService != null) {
            for (String path : servletsAndResources.keySet()) {
                Map info = servletsAndResources.get(path);

                if (info.containsKey(Servlet)) {
                    registerServlet(path, (Servlet)info.get(Servlet), (Dictionary)info.get(Dictionary), (HttpContext)info.get(HttpContext));
                } else if (info.containsKey(FilePath)) {
                    registerResources(path, (String)info.get(FilePath), (HttpContext)info.get(HttpContext));
                }
            }
        }
    }

    private void unregisterQueue() {
        if (servletsAndResources != null && httpService != null) {
            for (String path : servletsAndResources.keySet()) {
                unregister(path);
            }
        }
    }

    public void registerServlet(String path, Servlet servlet, Dictionary dictionary, HttpContext httpContext) {
        if (httpService != null) {
            try {
                httpService.registerServlet(path, servlet, dictionary, httpContext);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        Map info = new Hashtable<>();
        info.put(Servlet, servlet);
        if(dictionary != null) info.put(Dictionary, dictionary);
        if(httpContext != null)  info.put(HttpContext, httpContext);

        servletsAndResources.put(path, info);
    }

    public void registerResources(String path, String filePath, HttpContext httpContext) {
        if (httpService != null) {
            try {
                httpService.registerResources(path, filePath, httpContext);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        } else {
            Map info = new Hashtable<>();
            info.put(FilePath, filePath);
            if(httpContext != null)  info.put(HttpContext, httpContext);

            servletsAndResources.put(path, info);
        }
    }

    public void unregister(String path) {
        if (httpService != null) {
            try {
                httpService.unregister(path);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
