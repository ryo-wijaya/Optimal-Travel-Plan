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
public class SupportRequestNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>SupportRequestNotFoundException</code>
     * without detail message.
     */
    public SupportRequestNotFoundException() {
    }

    /**
     * Constructs an instance of <code>SupportRequestNotFoundException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public SupportRequestNotFoundException(String msg) {
        super(msg);
    }
}
