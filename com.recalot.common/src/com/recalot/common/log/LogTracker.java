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

package com.recalot.common.log;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import java.io.PrintStream;
import java.util.Date;

/**
 * @see org.osgi.service.indexer.osgi.LogTracker
 **/

public class LogTracker extends ServiceTracker<LogService,LogService> implements LogService {

    //TODO include log4j

    public LogTracker(BundleContext context) {
        super(context, LogService.class, null);

        open();
    }

    public void log(int level, String message) {
        log(null, level, message, null);
    }

    public void log(int level, String message, Throwable exception) {
        log(null, level, message, exception);
    }

    public void log(ServiceReference sr, int level, String message) {
        log(sr, level, message, null);
    }

    public void log(ServiceReference sr, int level, String message, Throwable exception) {
        LogService log = getService();

        if (log != null)
            log.log(sr, level, message, exception);
        else {
            PrintStream stream = (level <= LogService.LOG_WARNING) ? System.err : System.out;
            if (message == null)
                message = "";
            Date now = new Date();
            stream.println(String.format("[%-7s] %tF %tT: %s", LogUtils.formatLogLevel(level), now, now, message));
            if (exception != null)
                exception.printStackTrace(stream);
        }
    }
}