package edu.kit.kastel.trafficsimulation.objects;

/**
 * The interface for the class car
 * 
 * @author ufmkk
 * @version 1.0
 */
public interface ICar {
    /**
     * the method for updating the speed of the car
     * @param speedLimit the speed limit of the street the car is on
     */
    void updateSpeed(int speedLimit);

    /**
     * the method for updating the desired direction of the car
     */
    void updateDirection();
    
}
