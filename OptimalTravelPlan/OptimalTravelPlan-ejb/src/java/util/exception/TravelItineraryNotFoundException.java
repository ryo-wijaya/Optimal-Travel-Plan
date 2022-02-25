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
public class TravelItineraryNotFoundException extends Exception{

    /**
     * Creates a new instance of <code>TravelItineraryNotFoundException</code>
     * without detail message.
     */
    public TravelItineraryNotFoundException() {
    }

    /**
     * Constructs an instance of <code>TravelItineraryNotFoundException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public TravelItineraryNotFoundException(String msg) {
        super(msg);
    }
}
