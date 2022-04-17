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
public class DeletePaymentAccountException extends Exception {

    /**
     * Creates a new instance of <code>DeletePaymentAccountException</code>
     * without detail message.
     */
    public DeletePaymentAccountException() {
    }

    /**
     * Constructs an instance of <code>DeletePaymentAccountException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public DeletePaymentAccountException(String msg) {
        super(msg);
    }
}
