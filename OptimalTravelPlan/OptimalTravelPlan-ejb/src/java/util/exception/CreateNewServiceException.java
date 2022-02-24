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
public class CreateNewServiceException extends Exception {

    /**
     * Creates a new instance of <code>CreateNewServiceException</code> without
     * detail message.
     */
    public CreateNewServiceException() {
    }

    /**
     * Constructs an instance of <code>CreateNewServiceException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CreateNewServiceException(String msg) {
        super(msg);
    }
}
