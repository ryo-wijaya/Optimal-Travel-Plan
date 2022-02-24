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
public class DeleteServiceRateException extends Exception {

    /**
     * Creates a new instance of <code>DeleteServiceRateException</code> without
     * detail message.
     */
    public DeleteServiceRateException() {
    }

    /**
     * Constructs an instance of <code>DeleteServiceRateException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public DeleteServiceRateException(String msg) {
        super(msg);
    }
}
