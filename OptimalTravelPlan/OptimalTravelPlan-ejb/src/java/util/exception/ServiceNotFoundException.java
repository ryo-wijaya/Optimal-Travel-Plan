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
public class ServiceNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>ServiceNotFoundException</code> without
     * detail message.
     */
    public ServiceNotFoundException() {
    }

    /**
     * Constructs an instance of <code>ServiceNotFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ServiceNotFoundException(String msg) {
        super(msg);
    }
}
