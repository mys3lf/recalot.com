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

package com.recalot.tests.data.dummypage;

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
