/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author ryo20
 */
public class ServiceRateNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>ServiceRateNotFoundException</code>
     * without detail message.
     */
    public ServiceRateNotFoundException() {
    }

    /**
     * Constructs an instance of <code>ServiceRateNotFoundException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public ServiceRateNotFoundException(String msg) {
        super(msg);
    }
}
