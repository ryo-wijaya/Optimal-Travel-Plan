/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Entity;

/**
 *
 * @author sucram
 */
@Entity
public class Staff extends Account implements Serializable {

    private static final long serialVersionUID = 1L;

    public Staff() {
    }

    public Staff(String username, String password) {
        super(username, password);
    }

    public Long getStaffId() {
        return getAccountId();
    }

    public void setStaffId(Long staffId) {
        setAccountId(staffId);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getStaffId() != null ? getStaffId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the staffId fields are not set
        if (!(object instanceof Staff)) {
            return false;
        }
        Staff other = (Staff) object;
        if ((this.getStaffId() == null && other.getStaffId() != null) || (getStaffId() != null && !getStaffId().equals(other.getStaffId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Staff[ id=" + getStaffId() + " ]";
    }
    
}
