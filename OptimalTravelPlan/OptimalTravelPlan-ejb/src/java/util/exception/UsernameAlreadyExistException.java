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
public class UsernameAlreadyExistException extends Exception {

    /**
     * Creates a new instance of <code>UsernameAlreadyExistException</code>
     * without detail message.
     */
    public UsernameAlreadyExistException() {
    }

    /**
     * Constructs an instance of <code>UsernameAlreadyExistException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public UsernameAlreadyExistException(String msg) {
        super(msg);
    }
}
