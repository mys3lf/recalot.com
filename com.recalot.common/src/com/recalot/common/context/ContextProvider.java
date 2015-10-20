package com.recalot.common.context;

import com.recalot.common.GenericServiceListener;
import org.osgi.framework.BundleContext;

/**
 * Created by matthaeus.schmedding on 29.04.2015.
 */
public class ContextProvider extends GenericServiceListener<Context> {
    /**
     * Call the super constructor
     *
     * @param context   the bundle context
     */
    public ContextProvider(BundleContext context) {
        super(context, Context.class.getName());
    }

    //TODO: think about this
}
