/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author Anais
 */
public class DeleteBusinessException extends Exception {

    /**
     * Creates a new instance of <code>DeleteBusinessException</code> without
     * detail message.
     */
    public DeleteBusinessException() {
    }

    /**
     * Constructs an instance of <code>DeleteBusinessException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DeleteBusinessException(String msg) {
        super(msg);
    }
}
