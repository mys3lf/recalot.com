package com.example.web;


import com.example.cars.interfaces.ICar;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

import java.util.HashMap;

/**
 * @author Matthaeus.schmedding
 */
public class Controller implements ServiceListener {

    public HashMap<String, ICar> cars = new HashMap<>();
    private BundleContext context;

    public Controller(BundleContext context) {
        this.context = context;
    }

    public String get(String car) {
        StringBuilder builder = new StringBuilder();
        if (car != null) {
            if (cars.containsKey(car)) {
                builder.append(cars.get(car).getDesign());
            }
        } else {
            for (ICar instance : cars.values()) {
                builder.append(instance.getName() + ":" + instance.getDesign() + "\n");
            }
        }

        return builder.toString();
    }

    public void addCar(String id, ICar instance) {
        // Lock list and add service object.
        synchronized (cars) {
            if (!cars.containsKey(id)) cars.put(id, instance);
            System.out.println("Add id" + id + ":" + instance.getDesign());
        }
    }

    public void removeCar(String id) {
        // Lock list and add service object.
        synchronized (cars) {
            if (cars.containsKey(id)) {
                cars.remove(id);
                System.out.println("Remove car " + id);
            }
        }
    }

    public void updateCar(String id, ICar instance) {
        // Lock list and add service object.
        synchronized (cars) {
            cars.put(id, instance);
            System.out.println("Modify id" + id + ":" + instance.getDesign());
        }
    }

    @Override
    public void serviceChanged(ServiceEvent event) {
        String[] objectClass = (String[]) event.getServiceReference().getProperty("objectClass");

        if (objectClass[0].equals(ICar.class.getName())) {

            ICar car = (ICar) context.getService(event.getServiceReference());
            String serviceId = car.getName();

            if (event.getType() == ServiceEvent.REGISTERED) {
                addCar(serviceId, car);
            } else if (event.getType() == ServiceEvent.UNREGISTERING) {
                removeCar(serviceId);
            } else if (event.getType() == ServiceEvent.MODIFIED) {
                updateCar(serviceId, car);
            }
        }
    }

}
