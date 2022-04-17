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
public class UpdateBookingException extends Exception {

    /**
     * Creates a new instance of <code>UpdateBookingException</code> without
     * detail message.
     */
    public UpdateBookingException() {
    }

    /**
     * Constructs an instance of <code>UpdateBookingException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UpdateBookingException(String msg) {
        super(msg);
    }
}
