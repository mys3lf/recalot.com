package com.recalot.views.tracking;


import com.recalot.common.interfaces.controller.DataAccessController;
import com.recalot.common.interfaces.template.DataTemplate;
import com.recalot.common.interfaces.template.TemplateResult;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Matthaeus.schmedding
 */
public class ControllerHandler implements ServiceListener {

    private final Map<String, DataAccessController> dataAccessController;

    private BundleContext context;

    public ControllerHandler(BundleContext context) {
        this.context = context;
        this.dataAccessController = new LinkedHashMap<>();
    }


    @Override
    public void serviceChanged(ServiceEvent event) {
        String[] objectClass = (String[]) event.getServiceReference().getProperty("objectClass");

        if (objectClass[0].equals(DataAccessController.class.getName())) {
            String serviceId = (String) event.getServiceReference().getProperty(Constants.SERVICE_ID);
            DataAccessController instance = (DataAccessController) context.getService(event.getServiceReference());

            if (event.getType() == ServiceEvent.REGISTERED) {
                addDataAccessController(serviceId, instance);
            } else if (event.getType() == ServiceEvent.UNREGISTERING) {
                removeDataAccessController(serviceId);
            } else if (event.getType() == ServiceEvent.MODIFIED) {
                updateDataAccessController(serviceId, instance);
            }
        }
    }

    //region data access controller
    private DataAccessController getFirstDataAccessController() {
        // Lock list and add service object.
        synchronized (dataAccessController) {
            if (dataAccessController.values().size() > 0)
                return (DataAccessController) dataAccessController.values().toArray()[0];
        }

        return null;
    }

    private void updateDataAccessController(String id, DataAccessController instance) {
        // Lock list and add service object.
        synchronized (dataAccessController) {
            dataAccessController.put(id, instance);
            System.out.println("Update data access controller with id" + id);
        }
    }

    private void removeDataAccessController(String id) {
        // Lock list and add service object.
        synchronized (dataAccessController) {
            if (!dataAccessController.containsKey(id)) dataAccessController.remove(id);
            System.out.println("Remove data access controller with id" + id);
        }
    }

    private void addDataAccessController(String id, DataAccessController instance) {
        // Lock list and add service object.
        synchronized (dataAccessController) {
            if (!dataAccessController.containsKey(id)) dataAccessController.put(id, instance);
            System.out.println("Add data access controller with id" + id);
        }
    }
    //endregion

    public TemplateResult process(DataAccessController.RequestAction action, String templateKey) {
        return process(action, templateKey, null);
    }

    public TemplateResult process(DataAccessController.RequestAction action, String templateKey, Map<String, Object> params) {
        DataAccessController controller = getFirstDataAccessController();
        if (controller != null) {
            return controller.process(action, templateKey, params);
        }

        return null;
    }
}
