/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedBean;

import ejb.session.stateless.AccountSessionBeanLocal;
import entity.Account;
import entity.Business;
import entity.Staff;
import java.io.IOException;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import util.exception.AccountDisabledException;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author ryo20
 */
@Named(value = "loginPageManagedBean")
@RequestScoped
public class LoginPageManagedBean {

    @EJB
    private AccountSessionBeanLocal accountSessionBeanLocal;

    private String username;
    private String password;
    private FacesContext fc;

    /**
     * Creates a new instance of LoginPageManagedBean
     */
    public LoginPageManagedBean() {
        fc = FacesContext.getCurrentInstance();
    }

    public void login() throws IOException {
        try {
            Account loginAccount = accountSessionBeanLocal.login(username, password);
            fc.getExternalContext().getSessionMap().put("isLogin", true);
            fc.getExternalContext().getSessionMap().put("loggedInAccount", loginAccount);
            
            // So the growl on the next page can access the message
            fc.getExternalContext().getFlash().setKeepMessages(true);
            
            if (loginAccount instanceof Staff) {
                fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Login Successful!", null));
                fc.getExternalContext().redirect("adminMain.xhtml");
                
            } else if (loginAccount instanceof Business) {
                fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Login Successful!", null));
                fc.getExternalContext().redirect("businessMain.xhtml");
                
            } else {
                fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Invalid AccessRight for this portal!", null));
                fc.getExternalContext().redirect("error.xhtml");
            }
            
        } catch (InvalidLoginCredentialException ex) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Invalid Credentials Provided!", null));
        } catch (AccountDisabledException ex) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Account has been disabled!", null));
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
