package jsf.managedBean;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import ejb.session.stateless.AccountSessionBeanLocal;
import ejb.session.stateless.CountrySessionBeanLocal;
import entity.Business;
import entity.Country;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import util.exception.PasswordNotAcceptedException;
import util.exception.UnknownPersistenceException;
import util.exception.UsernameAlreadyExistException;

/**
 *
 * @author ryo20
 */
@Named(value = "registerPageManagedBean")
@RequestScoped
public class RegisterPageManagedBean {

    @EJB
    private CountrySessionBeanLocal countrySessionBeanLocal;

    @EJB
    private AccountSessionBeanLocal accountSessionBeanLocal;
    
    

    private String username;
    private String password;
    private String companyName;
    private String companyWebsite;
    private String companyNumber;
    private String companyAddress;
    private String businessEmail;
    private String selectedCountry;
    private String selectedCity;
    private Long postalCode;
    private List<String> countries;

    public RegisterPageManagedBean() {
    }

    @PostConstruct
    public void init() {
        countries = new ArrayList<>();
        List<Country> l1 = countrySessionBeanLocal.retrieveAllCountries();
        for(Country c : l1){
            countries.add(c.getName());
        }
    }

    public void register() throws IOException {
        try {
            if (this.selectedCountry != null && this.selectedCity != null && this.postalCode != null && this.postalCode < 1000000l && this.postalCode > 99999l) {
                this.companyAddress = selectedCountry + " " + selectedCity + " " + postalCode.toString();
                Business newBusiness = new Business(companyName, companyWebsite, companyNumber, companyAddress, username, password, businessEmail);
                accountSessionBeanLocal.createNewAccount(newBusiness);

                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("isLogin", true);
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("loggedInAccount", newBusiness);
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Registration Successful!", null));
                FacesContext.getCurrentInstance().getExternalContext().redirect("./businessPageFolder/businessMain.xhtml");
            } else {
                throw new Exception("Please ensure this is a valid address! (postal code 6 digits)");
            }
        } catch (PasswordNotAcceptedException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Password!", null));
        } catch (UsernameAlreadyExistException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Username already exists!", null));
        } catch (UnknownPersistenceException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Registration Failed", null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error : " + ex.getMessage(), null));

        }
    }

    public List<String> getCountries() {
        return countries;
    }

    public void setCountries(List<String> countries) {
        this.countries = countries;
    }

    public String getBusinessEmail() {
        return businessEmail;
    }

    public void setBusinessEmail(String businessEmail) {
        this.businessEmail = businessEmail;
    }

    public String getUsername() {
        return username;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getSelectedCountry() {
        return selectedCountry;
    }

    public void setSelectedCountry(String selectedCountry) {
        this.selectedCountry = selectedCountry;
    }

    public String getSelectedCity() {
        return selectedCity;
    }

    public void setSelectedCity(String selectedCity) {
        this.selectedCity = selectedCity;
    }

    public Long getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(Long postalCode) {
        this.postalCode = postalCode;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyWebsite() {
        return companyWebsite;
    }

    public void setCompanyWebsite(String companyWebsite) {
        this.companyWebsite = companyWebsite;
    }

}
