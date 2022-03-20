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
public class PasswordNotAcceptedException extends Exception {

    /**
     * Creates a new instance of <code>PasswordNotAcceptedException</code>
     * without detail message.
     */
    public PasswordNotAcceptedException() {
    }

    /**
     * Constructs an instance of <code>PasswordNotAcceptedException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public PasswordNotAcceptedException(String msg) {
        super(msg);
    }
}
