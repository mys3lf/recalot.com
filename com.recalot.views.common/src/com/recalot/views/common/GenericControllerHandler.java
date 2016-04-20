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


import com.recalot.common.GenericControllerListener;
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
