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
public class ChangePasswordException extends Exception {

    /**
     * Creates a new instance of <code>ChangePasswordException</code> without
     * detail message.
     */
    public ChangePasswordException() {
    }

    /**
     * Constructs an instance of <code>ChangePasswordException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ChangePasswordException(String msg) {
        super(msg);
    }
}
