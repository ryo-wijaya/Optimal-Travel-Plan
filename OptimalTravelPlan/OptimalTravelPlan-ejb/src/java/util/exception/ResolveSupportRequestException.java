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
public class ResolveSupportRequestException extends Exception {

    /**
     * Creates a new instance of <code>ResolveSupportRequestException</code>
     * without detail message.
     */
    public ResolveSupportRequestException() {
    }

    /**
     * Constructs an instance of <code>ResolveSupportRequestException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ResolveSupportRequestException(String msg) {
        super(msg);
    }
}
