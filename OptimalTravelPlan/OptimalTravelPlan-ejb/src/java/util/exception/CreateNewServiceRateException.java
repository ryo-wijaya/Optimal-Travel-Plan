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
public class CreateNewServiceRateException extends Exception {

    /**
     * Creates a new instance of <code>CreateNewServiceRateException</code>
     * without detail message.
     */
    public CreateNewServiceRateException() {
    }

    /**
     * Constructs an instance of <code>CreateNewServiceRateException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public CreateNewServiceRateException(String msg) {
        super(msg);
    }
}
