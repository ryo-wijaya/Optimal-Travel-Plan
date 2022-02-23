/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Customer;
import java.util.List;
import javax.ejb.Local;
import util.exception.AccountNotFoundException;
import util.exception.InvalidLoginCredentialsException;
import util.exception.UpdateCustomerException;

/**
 *
 * @author ryo20
 */
@Local
public interface CustomerSessionBeanLocal {

    public List<Customer> retrieveAllCustomers();

    public Customer retrieveCustomerById(Long customerId) throws AccountNotFoundException;

    public Customer retrieveCustomerByUsername(String username) throws AccountNotFoundException;

    public Customer doCustomerLogin(String username, String password) throws InvalidLoginCredentialsException;

    public void updateCustomer(Customer customer) throws AccountNotFoundException, UpdateCustomerException;
    
}
