/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedBean;

import ejb.session.stateless.AccountSessionBeanLocal;
import ejb.session.stateless.BusinessSessionBeanLocal;
import ejb.session.stateless.EmailSessionBeanLocal;
import entity.Account;
import entity.Business;
import entity.Staff;
import java.io.IOException;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import util.exception.AccountDisabledException;
import util.exception.AccountNotFoundException;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author ryo20
 */
@Named(value = "loginPageManagedBean")
@ViewScoped
public class LoginPageManagedBean implements Serializable {

    @EJB
    private EmailSessionBeanLocal emailSessionBeanLocal;

    @EJB
    private BusinessSessionBeanLocal businessSessionBeanLocal;

    @EJB
    private AccountSessionBeanLocal accountSessionBeanLocal;

    private String username;
    private String password;

    private String username2;
    private String password2;

    private String recoveryEmail;
    private FacesContext fc;

    /**
     * Creates a new instance of LoginPageManagedBean
     */
    public LoginPageManagedBean() {
    }

    @PostConstruct
    public void post() {
        this.password = "";
        this.password2 = "";
        this.username = "";
        this.username2 = "";
    }

    public void login(ActionEvent event) throws IOException {
        try {
            fc = FacesContext.getCurrentInstance();

            Account loginAccount;

            System.out.println("User & pass = " + username.isEmpty() + " " + password.isEmpty());
            if (!username.isEmpty() && !password.isEmpty()) {
                loginAccount = accountSessionBeanLocal.login(username, password);
            } else {
                loginAccount = accountSessionBeanLocal.login(username2, password2);
            }

            fc.getExternalContext().getSessionMap().put("isLogin", true);
            fc.getExternalContext().getSessionMap().put("loggedInAccount", loginAccount);

            // So the growl on the next page can access the message
            fc.getExternalContext().getFlash().setKeepMessages(true);

            if (loginAccount instanceof Staff) {
                fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Login Successful!", null));
                fc.getExternalContext().redirect("./adminPageFolder/adminMain.xhtml");

            } else if (loginAccount instanceof Business) {
                fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Login Successful!", null));
                fc.getExternalContext().redirect("./businessPageFolder/businessDashboard.xhtml");

            } else {
                fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Invalid AccessRight for this portal!", null));
                fc.getExternalContext().redirect("errorPage.xhtml");
            }

        } catch (InvalidLoginCredentialException ex) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Invalid Credentials Provided!", null));
            System.out.println("Exception details : " + ex.getMessage());
        } catch (AccountDisabledException ex) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Account has been disabled!", null));
        }
    }

    public void logout() throws IOException {
        fc = FacesContext.getCurrentInstance();
        fc.getExternalContext().invalidateSession();
        fc.getExternalContext().redirect("index.xhtml");
    }

    public void sendRecoveryEmail() {
        try {
            Business business = businessSessionBeanLocal.retrieveBusinessByEmail(recoveryEmail);
            String newPassword = accountSessionBeanLocal.forgetPasswordChange(business.getAccountId());

            String message = "Dear user" + ",\n\n"
                    + " please login with your username and the provided password below. Do proceed to change your password after. Thank you. " + "\n\n"
                    + "\n\nUsername: " + business.getUsername() + "\n\nNew password: " + newPassword;

            emailSessionBeanLocal.emailCheckoutNotificationAsync(message, recoveryEmail);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Recovery Email Sent!", null));
        } catch (AccountNotFoundException ex) {
            System.out.println("Email address " + recoveryEmail + " not found");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "No account associated with this email address", null));
        } catch (InterruptedException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unknown issue occured!", null));
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

    public String getRecoveryEmail() {
        return recoveryEmail;
    }

    public void setRecoveryEmail(String recoveryEmail) {
        this.recoveryEmail = recoveryEmail;
    }

    public String getUsername2() {
        return username2;
    }

    public void setUsername2(String username2) {
        this.username2 = username2;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

}
