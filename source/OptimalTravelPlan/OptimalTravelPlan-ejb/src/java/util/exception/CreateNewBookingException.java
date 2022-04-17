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
public class CreateNewBookingException extends Exception {

    /**
     * Creates a new instance of <code>CreateNewBookingException</code> without
     * detail message.
     */
    public CreateNewBookingException() {
    }

    /**
     * Constructs an instance of <code>CreateNewBookingException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CreateNewBookingException(String msg) {
        super(msg);
    }
}
