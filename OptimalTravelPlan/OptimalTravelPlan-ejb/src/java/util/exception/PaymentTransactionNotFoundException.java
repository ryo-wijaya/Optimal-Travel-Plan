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
public class PaymentTransactionNotFoundException extends Exception {

    /**
     * Creates a new instance of
     * <code>PaymentTransactionNotFoundException</code> without detail message.
     */
    public PaymentTransactionNotFoundException() {
    }

    /**
     * Constructs an instance of
     * <code>PaymentTransactionNotFoundException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public PaymentTransactionNotFoundException(String msg) {
        super(msg);
    }
}
