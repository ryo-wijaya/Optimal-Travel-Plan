/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedBean;

import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author sucram
 */
@Named(value = "adminTemplateManagedBean")
@RequestScoped
public class adminTemplateManagedBean {

    public adminTemplateManagedBean() {
    }

    public void redirectToCountryManagement() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("countryManagement.xhtml");
    }

    public void redirectToAddCountryManagement() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("addNewCountry", true);
        redirectToCountryManagement();
    }

    public void redirectToTagManagement() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("tagManagement.xhtml");
    }

    public void redirectToAddTagManagement() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("addNewTag", true);
        redirectToTagManagement();
    }

    public void redirectToAccountManagement() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("accountManagement.xhtml");
    }

    public void redirectToAddAccountManagement() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("addNewStaff", true);
        FacesContext.getCurrentInstance().getExternalContext().redirect("adminManagement.xhtml");
    }

    public void redirectToSupportManagement() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("adminSupportManagement.xhtml");
    }

    public void redirectToServiceManagement() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("serviceManagement.xhtml");
    }

    public void redirectToAddServiceManagement() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("addNewService", true);
        redirectToServiceManagement();
    }
}
