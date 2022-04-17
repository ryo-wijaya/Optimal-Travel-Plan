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
public class CreateSupportRequestException extends Exception {

    /**
     * Creates a new instance of <code>CreateSupportRequestException</code>
     * without detail message.
     */
    public CreateSupportRequestException() {
    }

    /**
     * Constructs an instance of <code>CreateSupportRequestException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public CreateSupportRequestException(String msg) {
        super(msg);
    }
}
