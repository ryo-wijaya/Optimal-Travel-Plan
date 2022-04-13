/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedBean;

import ejb.session.stateless.BusinessSessionBeanLocal;
import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.ServiceSessionBeanLocal;
import ejb.session.stateless.StaffSessionBeanLocal;
import ejb.session.stateless.TransactionSessionBeanLocal;
import ejb.session.stateless.TravelItinerarySessionBeanLocal;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;

/**
 *
 * @author ryo20
 */
@Named(value = "adminMainManagedBean")
@ViewScoped
public class AdminMainManagedBean implements Serializable {

    @EJB
    private ServiceSessionBeanLocal serviceSessionBeanLocal;

    @EJB
    private TravelItinerarySessionBeanLocal travelItinerarySessionBeanLocal;

    @EJB
    private TransactionSessionBeanLocal transactionSessionBeanLocal;

    @EJB
    private StaffSessionBeanLocal staffSessionBeanLocal;

    @EJB
    private BusinessSessionBeanLocal businessSessionBeanLocal;

    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;

    private DashboardModel model;
    
    private Integer customerCount;
    private Integer businessCount;
    private Integer staffCount;
    private Integer transactionCount;
    private Integer itineraryCount;
    private Integer serviceCount;

    public AdminMainManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {
        model = new DefaultDashboardModel();
        DashboardColumn column1 = new DefaultDashboardColumn();
        DashboardColumn column2 = new DefaultDashboardColumn();
        DashboardColumn column3 = new DefaultDashboardColumn();
        DashboardColumn column4 = new DefaultDashboardColumn();
        DashboardColumn column5 = new DefaultDashboardColumn();
        DashboardColumn column6 = new DefaultDashboardColumn();

        column1.addWidget("p1");
        column2.addWidget("p2");
        column3.addWidget("p3");
        column4.addWidget("p4");
        column5.addWidget("p5");
        column6.addWidget("p6");

        model.addColumn(column1);
        model.addColumn(column2);
        model.addColumn(column3);
        model.addColumn(column4);
        model.addColumn(column5);
        model.addColumn(column6);
        
        customerCount = customerSessionBeanLocal.retrieveAllCustomers().size();
        businessCount = businessSessionBeanLocal.retrieveAllBusinesses().size();
        staffCount = staffSessionBeanLocal.retrieveAllStaff().size();
        transactionCount = transactionSessionBeanLocal.retrieveAllPaymentTransaction().size();
        itineraryCount = transactionSessionBeanLocal.retrieveAllPaymentTransaction().size();
        serviceCount = serviceSessionBeanLocal.retrieveAllServices().size();
    }

    public DashboardModel getModel() {
        return model;
    }

    public void setModel(DashboardModel model) {
        this.model = model;
    }

    public Integer getCustomerCount() {
        return customerCount;
    }

    public void setCustomerCount(Integer customerCount) {
        this.customerCount = customerCount;
    }

    public Integer getBusinessCount() {
        return businessCount;
    }

    public void setBusinessCount(Integer businessCount) {
        this.businessCount = businessCount;
    }

    public Integer getStaffCount() {
        return staffCount;
    }

    public void setStaffCount(Integer staffCount) {
        this.staffCount = staffCount;
    }

    public Integer getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(Integer transactionCount) {
        this.transactionCount = transactionCount;
    }

    public Integer getItineraryCount() {
        return itineraryCount;
    }

    public void setItineraryCount(Integer itineraryCount) {
        this.itineraryCount = itineraryCount;
    }

    public Integer getServiceCount() {
        return serviceCount;
    }

    public void setServiceCount(Integer serviceCount) {
        this.serviceCount = serviceCount;
    }
}
