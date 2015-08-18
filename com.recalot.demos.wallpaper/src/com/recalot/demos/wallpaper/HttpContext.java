package com.recalot.demos.wallpaper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

/**
 * @author matthaeus.schmedding
 */
public class HttpContext implements org.osgi.service.http.HttpContext {

    private org.osgi.service.http.HttpContext defaultContext;

    public HttpContext(org.osgi.service.http.HttpContext defaultContext) {
        this.defaultContext = defaultContext;
    }

    @Override
    public boolean handleSecurity(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        return defaultContext.handleSecurity(httpServletRequest, httpServletResponse);
    }

    @Override
    public URL getResource(String s) {
        if(s != null && s.endsWith("/")) s+= "index.html";
        return defaultContext.getResource(s);
    }

    @Override
    public String getMimeType(String s) {
        return defaultContext.getMimeType(s);
    }
}
