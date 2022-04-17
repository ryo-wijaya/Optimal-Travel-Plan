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
import util.exception.DeleteStaffException;
import util.exception.UpdateStaffException;

/**
 *
 * @author ryo20
 */
@Stateless
public class StaffSessionBean implements StaffSessionBeanLocal {

    @PersistenceContext(unitName = "OptimalTravelPlan-ejbPU")
    private EntityManager em;
    private AccountSessionBeanLocal accountSessionBeanLocal;

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

    //Deleted staff login

    @Override
    public void updateStaff(Staff staff) throws AccountNotFoundException, UpdateStaffException {
        if (staff != null && staff.getStaffId() != null) {
            Staff staffToUpdate = this.retrieveStaffById(staff.getStaffId());
            staffToUpdate.setName(staff.getName());
            staffToUpdate.setUsername(staff.getUsername());
            em.flush();
        } else {
            throw new AccountNotFoundException("Staff ID not provided for staff to be updated");
        }
    }
    
@Override
    public void deleteStaff(Long staffId) throws AccountNotFoundException, DeleteStaffException{
        Staff staffToDelete = em.find(Staff.class, staffId);
        accountSessionBeanLocal.toggleAccountStatus(staffToDelete.getAccountId());
    }
    
}
