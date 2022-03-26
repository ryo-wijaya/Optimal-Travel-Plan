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

    public void redirectToCountryManagement() {

    }

    public void redirectToAddCountryManagement() {

    }

    public void redirectToTagManagement() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("tagManagement.xhtml");
    }

    public void redirectToAddTagManagement() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("addNewTag", true);
        redirectToTagManagement();
    }

    public void redirectToAccountManagement() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("accountManagaement.xhtml");
    }

    public void redirectToAddAccountManagement() {

    }

    public void redirectToSupportManagement() {

    }

    public void redirectToServiceManagement() {

    }

    public void redirectToAddServiceManagement() {

    }
}
