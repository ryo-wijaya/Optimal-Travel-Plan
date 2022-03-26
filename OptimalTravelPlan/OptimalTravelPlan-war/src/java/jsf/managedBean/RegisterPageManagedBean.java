package jsf.managedBean;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.inject.Named;
import javax.enterprise.context.RequestScoped;

/**
 *
 * @author ryo20
 */
@Named(value = "registerPageManagedBean")
@RequestScoped
public class RegisterPageManagedBean {
    
    

    private String username;
    private String password;
    private String companyName;
    private String companyWebsite;
    private String companyNumber;
    private String companyAddress;
    
    
    public RegisterPageManagedBean() {
    }
    
    public void register() {

    }
    
}
