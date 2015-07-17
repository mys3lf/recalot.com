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