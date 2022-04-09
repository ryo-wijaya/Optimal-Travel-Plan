/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.restful;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author sucram
 */
@javax.ws.rs.ApplicationPath("Resources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(ws.restful.AccountResource.class);
        resources.add(ws.restful.BookingResource.class);
        resources.add(ws.restful.PaymentAccountResource.class);
        resources.add(ws.restful.PaymentTransactionResource.class);
        resources.add(ws.restful.ReviewResource.class);
        resources.add(ws.restful.ServiceResource.class);
        resources.add(ws.restful.SupportRequestResource.class);
        resources.add(ws.restful.TravelItineraryResource.class);
    }
    
}
