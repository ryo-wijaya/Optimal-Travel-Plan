
package entity;

import java.io.Serializable;
import javax.persistence.Entity;
import util.enumeration.EmployeeRole;

@Entity
public class Staff extends Account implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String name;
    
    private EmployeeRole employeeRole;

    public Staff() {
    }

    public Staff(String username, String password, String name, EmployeeRole employeeRole) {
        super(username, password);
        this.name = name;
        this.employeeRole = employeeRole;
    }

    public Long getStaffId() {
        return getAccountId();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EmployeeRole getEmployeeRole() {
        return employeeRole;
    }

    public void setEmployeeRole(EmployeeRole employeeRole) {
        this.employeeRole = employeeRole;
    }
    
}
