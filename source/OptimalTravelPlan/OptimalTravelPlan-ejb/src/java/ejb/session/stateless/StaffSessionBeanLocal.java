/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Staff;
import java.util.List;
import javax.ejb.Local;
import util.exception.AccountNotFoundException;
import util.exception.DeleteStaffException;
import util.exception.InvalidLoginCredentialsException;
import util.exception.PasswordNotAcceptedException;
import util.exception.UpdateStaffException;

/**
 *
 * @author ryo20
 */
@Local
public interface StaffSessionBeanLocal {

    public List<Staff> retrieveAllStaff();

    public Staff retrieveStaffById(Long staffId) throws AccountNotFoundException;

    public Staff retrieveStaffByUsername(String username) throws AccountNotFoundException;

    public void updateStaff(Staff staff) throws AccountNotFoundException, UpdateStaffException;

    public void deleteStaff(Long staffId) throws AccountNotFoundException, DeleteStaffException;
}
