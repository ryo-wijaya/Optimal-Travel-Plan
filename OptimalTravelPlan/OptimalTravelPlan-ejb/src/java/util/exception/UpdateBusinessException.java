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
public class UpdateBusinessException extends Exception {

    /**
     * Creates a new instance of <code>UpdateBusinessException</code> without
     * detail message.
     */
    public UpdateBusinessException() {
    }

    /**
     * Constructs an instance of <code>UpdateBusinessException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UpdateBusinessException(String msg) {
        super(msg);
    }
}
