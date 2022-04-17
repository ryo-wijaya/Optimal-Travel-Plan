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
public class UpdateServiceException extends Exception {

    /**
     * Creates a new instance of <code>UpdateServiceException</code> without
     * detail message.
     */
    public UpdateServiceException() {
    }

    /**
     * Constructs an instance of <code>UpdateServiceException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UpdateServiceException(String msg) {
        super(msg);
    }
}
