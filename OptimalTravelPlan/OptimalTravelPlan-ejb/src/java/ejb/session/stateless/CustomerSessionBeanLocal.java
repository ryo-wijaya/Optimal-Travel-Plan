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
import util.exception.DeleteCustomerException;
import util.exception.InvalidLoginCredentialsException;
import util.exception.TagNotFoundException;
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

    public void updateCustomer(Customer customer) throws AccountNotFoundException, UpdateCustomerException;

    public void associateTagToCustomer(Long customerId, Long tagId) throws AccountNotFoundException, TagNotFoundException;

    public void deleteCustomer(Long businessId) throws AccountNotFoundException, DeleteCustomerException;

    public Customer retrieveCustomerByEmail(String email) throws AccountNotFoundException;
    
}