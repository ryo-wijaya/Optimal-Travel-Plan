/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author Jorda
 */
public class PaymentAccountNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>PaymentAccountNotFoundException</code>
     * without detail message.
     */
    public PaymentAccountNotFoundException() {
    }

    /**
     * Constructs an instance of <code>PaymentAccountNotFoundException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public PaymentAccountNotFoundException(String msg) {
        super(msg);
    }
}
