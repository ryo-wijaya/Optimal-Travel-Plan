/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author Jorda
 */
public class CountryNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>CountryNotFoundException</code> without
     * detail message.
     */
    public CountryNotFoundException() {
    }

    /**
     * Constructs an instance of <code>CountryNotFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CountryNotFoundException(String msg) {
        super(msg);
    }
}
