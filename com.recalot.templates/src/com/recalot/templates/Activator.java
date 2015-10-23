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

package com.recalot.templates;


import com.recalot.common.interfaces.template.*;
import com.recalot.templates.data.*;
import com.recalot.templates.experiments.*;
import com.recalot.templates.rec.*;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


/**
 * @author Matthaeus.schmedding
 */
public class Activator implements BundleActivator {


    /**
     * Implements BundleActivator.start(). Prints
     * a message and adds itself to the bundle context as a service
     * listener.
     *
     * @param context the framework context for the bundle.
     */
    public void start(BundleContext context) {
        context.registerService(DataTemplate.class.getName(), new JsonDataTemplate(), null);
        context.registerService(DataTemplate.class.getName(), new XmlDataTemplate(), null);

        context.registerService(RecommenderTemplate.class.getName(), new JsonRecommenderTemplate(), null);
        context.registerService(RecommenderTemplate.class.getName(), new XmlRecommenderTemplate(), null);

        context.registerService(ExperimentTemplate.class.getName(), new JsonExperimentTemplate(), null);
    }

    /**
     * Implements BundleActivator.stop(). Prints
     * a message and removes itself from the bundle context as a
     * service listener.
     *
     * @param context the framework context for the bundle.
     */
    public void stop(BundleContext context) throws Exception {
        //nothing to do
    }
}