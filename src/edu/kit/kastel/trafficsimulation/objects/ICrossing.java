package edu.kit.kastel.trafficsimulation.objects;

/**
 * The interface for the class Crossing
 * 
 * @author ufmkk
 * @version 1.0
 */
public interface ICrossing {

    /**
     * the method for adding a new entering street
     * @param id the id of the entering street
     */
    void addEnterStreet(int id);

    /**
     * the method for adding a new exiting street
     * @param id the id of the exiting street
     */
    void addExitStreet(int id);

    /**
     * the method for updating the crossing
     */
    void update();
    
}
