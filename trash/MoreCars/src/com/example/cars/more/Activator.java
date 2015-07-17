package com.example.cars.more;

/**
 * @author Matthaeus.schmedding
 */

import com.example.cars.interfaces.ICar;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.Hashtable;

/**
 * @author Matthaeus.schmedding
 */
public class Activator implements BundleActivator
{

    /**
     * Implements BundleActivator.start(). Prints
     * a message and adds itself to the bundle context as a service
     * listener.
     * @param context the framework context for the bundle.
     **/
    public void start(BundleContext context)
    {

        //   context.addServiceListener(this);

        Hashtable<String, String> props = new Hashtable<>();
        props.put("Type", "MonsterTruck");
        context.registerService(ICar.class.getName(), new MonsterTruck(), props);

        Hashtable<String, String> props2 = new Hashtable<>();
        props2.put("Type", "Astra");
        context.registerService(ICar.class.getName(), new Astra(), props2);
    }

    /**
     * Implements BundleActivator.stop(). Prints
     * a message and removes itself from the bundle context as a
     * service listener.
     * @param context the framework context for the bundle.
     **/
    public void stop(BundleContext context)
    {
        //  context.removeServiceListener(this);
        // Note: It is not required that we remove the listener here,
        // since the framework will do it automatically anyway.
    }


}