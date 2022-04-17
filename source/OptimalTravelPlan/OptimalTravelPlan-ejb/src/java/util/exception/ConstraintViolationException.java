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
public class ConstraintViolationException extends Exception {

    /**
     * Creates a new instance of <code>ConstraintViolationException</code>
     * without detail message.
     */
    public ConstraintViolationException() {
    }

    /**
     * Constructs an instance of <code>ConstraintViolationException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public ConstraintViolationException(String msg) {
        super(msg);
    }
}
