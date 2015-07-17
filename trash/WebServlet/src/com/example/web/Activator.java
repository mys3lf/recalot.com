package com.example.web;

import com.example.cars.interfaces.ICar;
import org.osgi.framework.*;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTrackerCustomizer;


/**
 * @author Matthaeus.schmedding
 */
public class Activator implements BundleActivator {
    private ServiceTracker httpTracker;
    private Controller controller;

    public void start(BundleContext context) throws Exception {
        controller = new Controller(context);

        checkForCars(context);
        context.addServiceListener(controller);

        httpTracker = new ServiceTracker(context, HttpService.class.getName(), null, controller);
        // start tracking all HTTP services...
        httpTracker.open();
    }

    private void checkForCars(BundleContext context) throws InvalidSyntaxException {
        ServiceReference[] refs = context.getServiceReferences(ICar.class.getName(), "(Type=*)");

        if (refs != null) {
            for (ServiceReference service : refs) {
                ICar car = (ICar) context.getService(service);
                controller.addCar(car.getName(), car);
            }
        }
    }

    public void stop(BundleContext context) throws Exception {
        context.removeServiceListener(controller);

        // stop tracking all HTTP services...
        httpTracker.close();
    }
}
