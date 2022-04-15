/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedBean;

import ejb.session.stateless.BookingSessionBeanLocal;
import ejb.session.stateless.ServiceSessionBeanLocal;
import entity.Account;
import entity.Booking;
import entity.Service;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.BarChartSeries;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.PieChartModel;

/**
 *
 * @author Jorda
 */
@Named(value = "businessDashBoardManagedBean")
@ViewScoped
public class businessDashBoardManagedBean implements Serializable {

    @EJB(name = "BookingSessionBeanLocal")
    private BookingSessionBeanLocal bookingSessionBeanLocal;

    @EJB
    private ServiceSessionBeanLocal serviceSessionBeanLocal;

    private PieChartModel model1;
    
    private BarChartModel model2;
    
    private BarChartModel model3;
    
    /**
     * Creates a new instance of businessDashBoardManagedBean
     */
    public businessDashBoardManagedBean() {
    }
    
    @PostConstruct
    public void init() {
        Account user = (Account) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("loggedInAccount");
        List<Service> services = serviceSessionBeanLocal.retrieveAllActiveServiceByBusinessId(user.getAccountId());
        model1 = new PieChartModel();
        for (Service s: services) {
            model1.set(s.getServiceName(), bookingSessionBeanLocal.retrieveBookingsByServiceId(s.getServiceId()).size());
        }
        
        model1.setTitle("Services");
        model1.setLegendPosition("s");
        model1.setShowDatatip(true);
        model1.setShowDataLabels(true);
        model1.setDataFormat("value");
        
        model2 = new BarChartModel();
        ChartSeries ser = new ChartSeries();
        ser.setLabel("Services");
        for(Service s: services) {
            ser.set(s.getServiceName(), s.getRating());
        }
        model2.addSeries(ser);
        model2.setTitle("Ratings");
        
        model3 = new BarChartModel();
        BarChartSeries ser2 = new BarChartSeries();
        ser2.setLabel("Services");
        for(Service s: services) {
            List<Booking> bookings = bookingSessionBeanLocal.retrieveBookingsByServiceId(s.getServiceId());
            double count = 0.0;
            for(Booking b: bookings) {
                count += b.getPaymentTransaction().getPrevailingRateAtPaymentDate().doubleValue();
            }
            ser2.set(s.getServiceName(), count);
            count = 0;
        }
        model3.addSeries(ser2);
        model3.setTitle("Income");
        
    }
    
    public PieChartModel getModel1() {
        return model1;
    }
    
    public BarChartModel getModel2() {
        return model2;
    }
    
    public BarChartModel getModel3() {
        return model3;
    }
}
