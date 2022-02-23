/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Staff;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.AccountNotFoundException;
import util.exception.InvalidLoginCredentialsException;
import util.exception.UpdateStaffException;

/**
 *
 * @author ryo20
 */
@Stateless
public class StaffSessionBean implements StaffSessionBeanLocal {

    @PersistenceContext(unitName = "OptimalTravelPlan-ejbPU")
    private EntityManager em;

    @Override
    public List<Staff> retrieveAllStaff() {
        Query query = em.createQuery("SELECT s FROM Staff s");
        return query.getResultList();
    }

    @Override
    public Staff retrieveStaffById(Long staffId) throws AccountNotFoundException {
        Staff staff = em.find(Staff.class, staffId);
        if (staff != null) {//lazy loading
            return staff;
        } else {
            throw new AccountNotFoundException();
        }
    }

    @Override
    public Staff retrieveStaffByUsername(String username) throws AccountNotFoundException {
        Query query = em.createQuery("SELECT s FROM Staff s WHERE s.username = :inUsername");
        query.setParameter("inUsername", username);
        try {
            Staff staff = (Staff) query.getSingleResult();
            return staff;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new AccountNotFoundException("Username has no match!");
        }
    }

    @Override
    public Staff doStaffLogin(String username, String password) throws InvalidLoginCredentialsException {
        try {
            Staff staff = retrieveStaffByUsername(username);
            String passwordHash = new String(staff.doMD5Hashing(password + staff.getSalt()));

            if (passwordHash.equals(staff.getPassword())) {
                return staff;
            } else {
                throw new InvalidLoginCredentialsException();
            }
        } catch (AccountNotFoundException ex) {
            throw new InvalidLoginCredentialsException();
        }
    }

    @Override
    public void updateStaff(Staff staff) throws AccountNotFoundException, UpdateStaffException {
        if (staff != null && staff.getStaffId() != null) {
            Staff staffToUpdate = this.retrieveStaffById(staff.getStaffId());

            if (staffToUpdate.getUsername().equals(staff.getUsername())) {
                staffToUpdate.setName(staff.getName());
                staffToUpdate.setEmployeeRole(staff.getEmployeeRole());
            } else {
                throw new UpdateStaffException("Username of staff record to be updated does not match the existing record");
            }
        } else {
            throw new AccountNotFoundException("Staff ID not provided for staff to be updated");
        }
    }
}
