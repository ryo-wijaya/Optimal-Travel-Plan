/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedBean;

import ejb.session.stateless.CountrySessionBeanLocal;
import ejb.session.stateless.ServiceSessionBeanLocal;
import ejb.session.stateless.TagSessionBeanLocal;
import entity.Account;
import entity.Business;
import entity.Country;
import entity.Service;
import entity.Tag;
import java.io.IOException;
import java.io.InputStream;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.Part;
import org.primefaces.PrimeFaces;
import util.enumeration.ServiceType;
import util.exception.ServiceNotFoundException;

@Named(value = "serviceManagementManagedBean")
@ViewScoped
public class serviceManagementManagedBean implements Serializable {

    private String PATH = "C:\\Images\\";
    private Part uploadedImage;
    private String fileName;
    private String localFile;
    private String GLASSFISH_PATH = "C:\\glassfish-5.1.0\\glassfish\\domains\\domain1\\generated\\jsp\\OptimalTravelPlan\\OptimalTravelPlan-war_war\\";

    @EJB
    private CountrySessionBeanLocal countrySessionBeanLocal;

    @EJB
    private TagSessionBeanLocal tagSessionBeanLocal;

    @EJB
    private ServiceSessionBeanLocal serviceSessionBeanLocal;

    private List<Service> services;
    private List<Service> filteredServices;
    private Boolean filtered;
    private Service newService;

    private List<Long> tagsSelected;
    private List<Tag> allTags;

    private List<Country> allCountries;
    private Long selectedCountry;
    private Boolean requireVac;
    private Account loggedInAccount;
    private Service selectedService;
    private List<ServiceType> allServiceTypes;
    private ServiceType selectedServiceType;
    private Business businessToView;

    public serviceManagementManagedBean() {
        allServiceTypes = new ArrayList<>();
        allServiceTypes.add(ServiceType.HOTEL);
        allServiceTypes.add(ServiceType.ENTERTAINMENT);
        allServiceTypes.add(ServiceType.FOOD_AND_BEVERAGE);
        allServiceTypes.add(ServiceType.VEHICLE_RENTAL);
        this.fileName = "";
        this.localFile = "";
    }

    @PostConstruct
    public void post() {
        List<Service> list = (List<Service>) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("servicesToView");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("servicesToView");
        loggedInAccount = (Account) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("loggedInAccount");
        if (list != null) {
            services = list;
            filtered = true;
        } else {
            if (loggedInAccount instanceof Business) {
                refreshBusinessServiceList();
            } else {
                refreshServicesList(null);
            }
        }

        businessToView = new Business();
        this.allCountries = countrySessionBeanLocal.retrieveAllCountries();
        this.allTags = tagSessionBeanLocal.retrieveAllTags();
        this.tagsSelected = new ArrayList<>();
        this.newService = new Service();

        Boolean addNService = (Boolean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("addNewService");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("addNewService");
        if (addNService != null && addNService) {
            PrimeFaces.current().executeScript("PF('dialogCreateNewService').show();");
        }
    }

    public void refreshServicesList(ActionEvent event) {
        this.services = serviceSessionBeanLocal.retrieveAllServices();
        this.filtered = false;
    }

    public void refreshBusinessServiceList() {
        this.services = serviceSessionBeanLocal.retrieveAllServiceByBusinessId(loggedInAccount.getAccountId());
        this.filtered = false;
    }

    public void viewServiceOwner(ActionEvent event) throws IOException {
        this.businessToView = (Business) event.getComponent().getAttributes().get("businessToView");
    }

    public void toggleServiceActive(ActionEvent event) throws ServiceNotFoundException {
        Service service = (Service) event.getComponent().getAttributes().get("serviceToToggle");
        Boolean temp = service.getActive();
        if (temp) {
            service.setActive(false);
        } else {
            service.setActive(true);
        }
        serviceSessionBeanLocal.toggleServiceActivation(service.getServiceId());
    }

    public void createNewNonBusinessService(ActionEvent event) {
        try {
            if (selectedCountry == null) {
                throw new Exception("Please Select a country!");
            }
            if (tagsSelected == null || tagsSelected.size() < 1) {
                throw new Exception("Please select at least 1 tag!");
            }
            Long s = 0l;
            newService.setRequireVaccination(requireVac);
            newService.setServiceType(ServiceType.ENTERTAINMENT);
            s = serviceSessionBeanLocal.createNewService(newService, 1l, tagsSelected, selectedCountry);
            newService.setServiceId(s);
            this.services.add(newService);
            newService = new Service();
            selectedCountry = null;
            tagsSelected = null;
            requireVac = null;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully Created service ID = " + s, null));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating the new product: " + e.getMessage(), null));
        }
    }

    public void createNewBusinessService(ActionEvent event) {
        try {
            if (selectedCountry == 0) {
                throw new Exception("Please Select a country!");
            }
            System.out.println("jsf.managedBean.serviceManagementManagedBean.createNewBusinessService()" + selectedCountry);
            if (tagsSelected == null || tagsSelected.size() < 1) {
                throw new Exception("Please select at least 1 tag!");
            }
            newService.setRequireVaccination(requireVac);
            newService.setServiceType(selectedServiceType);
            System.out.println("ACCOUNT ID CREATION: " + loggedInAccount.getAccountId());
            Long s = serviceSessionBeanLocal.createNewService(newService, loggedInAccount.getAccountId(), tagsSelected, selectedCountry);
            newService.setServiceId(s);
            this.services.add(newService);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("selectedService", newService);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("addNewServiceRate", true);
            FacesContext.getCurrentInstance().getExternalContext().redirect("./serviceRateManagement.xhtml");
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating the new service: " + e.getMessage(), null));
        }
    }

    public void editServiceFees(ActionEvent event) {
        try {
            Service service = (Service) event.getComponent().getAttributes().get("serviceRateEditSelected");
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("selectedService", service);
            FacesContext.getCurrentInstance().getExternalContext().redirect("./serviceRateManagement.xhtml");
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Navigation fail! : " + ex.getMessage(), null));
        }
    }

    public void doUpdateService(ActionEvent event) {
        this.selectedService = (Service) event.getComponent().getAttributes().get("serviceToUpdate");
        this.requireVac = selectedService.getRequireVaccination();
        this.selectedCountry = selectedService.getCountry().getCountryId();
        this.tagsSelected = new ArrayList<>();
        for (Tag t : selectedService.getTags()) {
            tagsSelected.add(t.getTagId());
        }
        this.selectedServiceType = selectedService.getServiceType();
    }

    public void updateService(ActionEvent event) {
        try {
            selectedService.setServiceType(selectedServiceType);
            Service s = serviceSessionBeanLocal.updateService(selectedService, tagsSelected, selectedCountry, selectedService.getRequireVaccination());
            selectedService.setRequireVaccination(s.getRequireVaccination());
            selectedService.setTags(s.getTags());
            selectedService.setCountry(s.getCountry());

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Service Updated", null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Update Values! Error: " + ex.getMessage(), null));
        }
    }

    public void updateImage(ActionEvent event) {
        try {
            if (uploadedImage != null) {
                if (!uploadedImage.getContentType().contains("image/png")) {
                    throw new Exception("Image type can only be png!");
                }

                for (String c : uploadedImage.getHeader("content-disposition").split(";")) {
                    if (c.trim().startsWith("filename")) {
                        String fn = c.substring(c.indexOf('=') + 1).trim().replace("\"", "");
                        this.fileName = fn.substring(fn.lastIndexOf("/") + 1).substring(fn.lastIndexOf("\\") + 1);
                        break;
                    }
                }
                Path file = Files.createTempFile(Paths.get("/otp/Images"), this.fileName + "-", ".png");

                try (InputStream input = uploadedImage.getInputStream()) {
                    Files.copy(input, file, StandardCopyOption.REPLACE_EXISTING);
                }

                this.localFile = file.getFileName().toString();

                System.out.println("jsf.managedBean.serviceManagementManagedBean.updateImage() file uploaded to c:/otp/Images/filename = " + this.localFile);
                //uploadedImage.write(PATH+fileName);
                //Cannot upload image! 
                //Error: C:\glassfish-5.1.0\glassfish\domains\domain1\generated\jsp\OptimalTravelPlan\OptimalTravelPlan-war_war\C:\Images\service.png 
                //(The filename, directory name, or volume label syntax is incorrect)
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, fileName + " Uploaded!", null));
            }
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot upload image! Error: " + ex.getMessage(), null));
        }
    }

    public String getLocalFile() {
        return localFile;
    }

    public void setLocalFile(String localFile) {
        this.localFile = localFile;
    }

    public Service getSelectedService() {
        return selectedService;
    }

    public void setSelectedService(Service selectedService) {
        this.selectedService = selectedService;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public Boolean getRequireVac() {
        return requireVac;
    }

    public Business getBusinessToView() {
        return businessToView;
    }

    public void setBusinessToView(Business businessToView) {
        this.businessToView = businessToView;
    }

    public void setRequireVac(Boolean requireVac) {
        this.requireVac = requireVac;
    }

    public List<Service> getFilteredServices() {
        return filteredServices;
    }

    public void setFilteredServices(List<Service> filteredServices) {
        this.filteredServices = filteredServices;
    }

    public Boolean getFiltered() {
        return filtered;
    }

    public void setFiltered(Boolean filtered) {
        this.filtered = filtered;
    }

    public Service getNewService() {
        return newService;
    }

    public void setNewService(Service newService) {
        this.newService = newService;
    }

    public List<Long> getTagsSelected() {
        return tagsSelected;
    }

    public void setTagsSelected(List<Long> tagsSelected) {
        this.tagsSelected = tagsSelected;
    }

    public List<Tag> getAllTags() {
        return allTags;
    }

    public void setAllTags(List<Tag> allTags) {
        this.allTags = allTags;
    }

    public List<Country> getAllCountries() {
        return allCountries;
    }

    public void setAllCountries(List<Country> allCountries) {
        this.allCountries = allCountries;
    }

    public Long getSelectedCountry() {
        return selectedCountry;
    }

    public void setSelectedCountry(Long selectedCountry) {
        this.selectedCountry = selectedCountry;
    }

    public Account getLoggedInAccount() {
        return loggedInAccount;
    }

    public void setLoggedInAccount(Account loggedInAccount) {
        this.loggedInAccount = loggedInAccount;
    }

    public Boolean isBusiness() {
        return this.loggedInAccount instanceof Business;
    }

    public List<ServiceType> getAllServiceTypes() {
        return allServiceTypes;
    }

    public void setAllServiceTypes(List<ServiceType> allServiceTypes) {
        this.allServiceTypes = allServiceTypes;
    }

    public ServiceType getSelectedServiceType() {
        return selectedServiceType;
    }

    public void setSelectedServiceType(ServiceType selectedServiceType) {
        this.selectedServiceType = selectedServiceType;
    }

    public Part getUploadedImage() {
        return uploadedImage;
    }

    public void setUploadedImage(Part uploadedImage) {
        this.uploadedImage = uploadedImage;
    }

}
