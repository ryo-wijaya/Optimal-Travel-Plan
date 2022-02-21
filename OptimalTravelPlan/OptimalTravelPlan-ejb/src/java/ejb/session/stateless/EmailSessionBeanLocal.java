/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import java.util.concurrent.Future;
import javax.ejb.Local;

/**
 *
 * @author sucram
 */
@Local
public interface EmailSessionBeanLocal {

    public Boolean emailCheckoutNotificationSync(String content, String toEmailAddress);

    public Future<Boolean> emailCheckoutNotificationAsync(String content, String toEmailAddress) throws InterruptedException;
    
}
