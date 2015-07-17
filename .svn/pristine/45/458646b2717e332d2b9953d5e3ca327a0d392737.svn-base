package com.example.cars.consume;

import com.example.cars.interfaces.ICar;
import org.osgi.framework.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * @author Matthaeus.schmedding
 */
public class Activator implements BundleActivator , ServiceListener
{
    private BundleContext context;

    /**
     * Implements BundleActivator.start(). Queries for
     * all available dictionary services. If none are found it
     * simply prints a message and returns, otherwise it reads
     * words from standard input and checks for their existence
     * from the first dictionary that it finds.
     * (NOTE: It is very bad practice to use the calling thread
     * to perform a lengthy process like this; this is only done
     * for the purpose of the tutorial.)
     * @param context the framework context for the bundle.
     **/
    public void start(BundleContext context) throws Exception
    {
        context.addServiceListener(this);

        this.context = context;
        // Query for all service references matching any language.
        ServiceReference[] refs = context.getServiceReferences(ICar.class.getName(), "(Type=*)");

        if (refs != null)
        {
            System.out.println("Found " + refs.length + " services");

            for(ServiceReference service : refs)
            {
                ICar car = (ICar) context.getService(service);

                System.out.println("Found car:" + car.getDesign() + ":" +  car.getClass().getName());
            }
        }
        else
        {
            System.out.println("Couldn't find any dictionary service...");
        }
    }

    /**
     * Implements BundleActivator.stop(). Does nothing since
     * the framework will automatically unget any used services.
     * @param context the framework context for the bundle.
     **/
    public void stop(BundleContext context)
    {
        context.removeServiceListener(this);
        // NOTE: The service is automatically released.
    }

    /**
     * Implements ServiceListener.serviceChanged().
     * Prints the details of any service event from the framework.
     * @param event the fired service event.
     **/
    public void serviceChanged(ServiceEvent event)
    {
        if (event.getType() == ServiceEvent.REGISTERED)
        {
            if(event.getServiceReference().getProperty("Car") == "true"){
                ICar car = (ICar) context.getService(event.getServiceReference());
                System.out.println("New car registered:" + car.getDesign() + ":" +  car.getClass().getName());
            }
        }
        else if (event.getType() == ServiceEvent.UNREGISTERING)
        {

        }
        else if (event.getType() == ServiceEvent.MODIFIED)
        {

        }
    }
}