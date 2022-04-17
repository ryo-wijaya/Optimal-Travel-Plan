/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author sucram
 */
public class CustomerNotMatchException extends Exception {

    /**
     * Creates a new instance of <code>CustomerNotMatchException</code> without
     * detail message.
     */
    public CustomerNotMatchException() {
    }

    /**
     * Constructs an instance of <code>CustomerNotMatchException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CustomerNotMatchException(String msg) {
        super(msg);
    }
}
