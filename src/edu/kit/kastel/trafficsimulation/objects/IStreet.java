package edu.kit.kastel.trafficsimulation.objects;

import edu.kit.kastel.trafficsimulation.Simulation;

/**
 * The interface for the class Street
 * 
 * @author ufmkk
 * @version 1.0
 */
public interface IStreet {

    /**
     * The method for adding cars that come from other streets
     * @param car the car that is added
     */
    void addCar(Car car);

    /**
     * The method for placing the cars at the start
     * @param car the added car
     * @return an error message, if one occurs; null, otherwise
     */
    String placeCar(Car car);

    /**
     * The method for finding the car with the given id
     * @param id the given id
     * @return the car, if it exists; null, otherwise
     */
    Car findCar(int id);

    /**
     * The method for updating the street
     *
     * @param simulation the {@link Simulation} the street is a part of
     */
    void update(Simulation simulation);
    
}
