/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author Anais
 */
public class BookingNotMatchException extends Exception {

    /**
     * Creates a new instance of <code>BookingNotMatchException</code> without
     * detail message.
     */
    public BookingNotMatchException() {
    }

    /**
     * Constructs an instance of <code>BookingNotMatchException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public BookingNotMatchException(String msg) {
        super(msg);
    }
}
