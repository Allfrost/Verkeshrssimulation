package edu.kit.kastel.trafficsimulation;

import edu.kit.kastel.trafficsimulation.objects.Car;
import edu.kit.kastel.trafficsimulation.objects.Crossing;
import edu.kit.kastel.trafficsimulation.objects.Street;

/**
 * The interface for the class Simulation
 * 
 * @author ufmkk
 * @version 1.0
 */
public interface ISimulation {

    /**
     * The method for finding the crossing with the given id
     *
     * @param id the given id
     * @return the crossing with the given id, if it does exist; null, if it does not
     */
    Crossing findCrossing(int id);

    /**
     * The method for finding the street with the given id
     *
     * @param id the given id
     * @return the street with the given id, if it does exist; null, if it does not
     */
    Street findStreet(int id);

    /**
     * The method for finding the car with the given id
     *
     * @param id the given id
     * @return the car with the given id, if it does exist; null, if it does not
     */
    Car findCar(int id);

    /**
     * The method fot getting the position of the cr with the given id
     *
     * @param carId the given car id
     * @return the position, street and the speed of the car in the correct format
     */
    String giveCarPosition(int carId);

    /**
     * The method for advancing the simulation for the given ticks
     *
     * @param ticks How many ticks the simulation will be advanced
     * @return An error message, if an error occurs; Ready, otherwise
     */
    String update(int ticks);
    
}
