/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Entity
public class Business extends Account implements Serializable {

    private static final long serialVersionUID = 1L;

    @OneToMany(mappedBy = "business", fetch = FetchType.LAZY)
    private List<Service> services;

    private String companyName;

    private String companyWebsite;

    private String companyNumber;

    private String headquarterAddress;

    public Business() {
    }

    public Business(String companyName, String companyWebsite, String companyNumber, String headquarterAddress, String username, String password) {
        super(username, password);
        this.companyName = companyName;
        this.companyWebsite = companyWebsite;
        this.companyNumber = companyNumber;
        this.headquarterAddress = headquarterAddress;
        this.services = new ArrayList<>();
    }

    public Long getBusinessId() {
        return getAccountId();
    }

    public void setBusinessId(Long businessId) {
        setAccountId(businessId);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getAccountId() != null ? getAccountId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the getAccountId() fields are not set
        if (!(object instanceof Business)) {
            return false;
        }
        Business other = (Business) object;
        if ((this.getAccountId() == null && other.getAccountId() != null) || (this.getAccountId() != null && !this.getAccountId().equals(other.getAccountId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Business[ id=" + getAccountId() + " ]";
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

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getHeadquarterAddress() {
        return headquarterAddress;
    }

    public void setHeadquarterAddress(String headquarterAddress) {
        this.headquarterAddress = headquarterAddress;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public void addService(Service service) {
        services.add(service);
    }

    public void removeService(Service service) {
        services.remove(service);
    }
}
