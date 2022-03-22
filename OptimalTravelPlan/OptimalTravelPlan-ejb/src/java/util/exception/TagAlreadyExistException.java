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
public class TagAlreadyExistException extends Exception{

    /**
     * Creates a new instance of <code>TagAlreadyExistException</code> without
     * detail message.
     */
    public TagAlreadyExistException() {
    }

    /**
     * Constructs an instance of <code>TagAlreadyExistException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public TagAlreadyExistException(String msg) {
        super(msg);
    }
}
