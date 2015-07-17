package com.recalot.views.common;


import com.recalot.common.communication.TemplateResult;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.controller.Controller;
import com.recalot.common.interfaces.controller.RequestAction;
import org.osgi.framework.BundleContext;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Matthaeus.schmedding
 */
public class GenericControllerHandler<T> implements Closeable {

    private final Map<Long, T> dataAccessController;
    private final GenericControllerListener listener;

    private BundleContext context;

    public GenericControllerHandler(BundleContext context, String className) {
        this.context = context;
        this.dataAccessController = new LinkedHashMap<>();
        this.listener = new GenericControllerListener(context, className);
        context.addServiceListener(this.listener);
    }

    public TemplateResult process(RequestAction action, String templateKey) {
        return process(action, templateKey, null);
    }

    public TemplateResult process(RequestAction action, String templateKey, Map<String, String> params) {
        Controller controller = listener.getFirstInstance();
        if (controller != null) {
            try {
                return controller.process(action, templateKey, params);
            } catch (BaseException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public void close() throws IOException {
        if (listener != null) {
            context.removeServiceListener(this.listener);
        }
    }
}
