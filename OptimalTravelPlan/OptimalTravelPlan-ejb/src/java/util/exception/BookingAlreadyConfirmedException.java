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
public class BookingAlreadyConfirmedException extends Exception{

    /**
     * Creates a new instance of <code>BookingAlreadyConfirmedException</code>
     * without detail message.
     */
    public BookingAlreadyConfirmedException() {
    }

    /**
     * Constructs an instance of <code>BookingAlreadyConfirmedException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public BookingAlreadyConfirmedException(String msg) {
        super(msg);
    }
}
