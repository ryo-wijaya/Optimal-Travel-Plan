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
public class UpdateTravelItineraryException extends Exception{

    /**
     * Creates a new instance of <code>UpdateTravelItineraryException</code>
     * without detail message.
     */
    public UpdateTravelItineraryException() {
    }

    /**
     * Constructs an instance of <code>UpdateTravelItineraryException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public UpdateTravelItineraryException(String msg) {
        super(msg);
    }
}
