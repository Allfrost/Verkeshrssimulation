package edu.kit.kastel.trafficsimulation.objects;

import edu.kit.kastel.trafficsimulation.Main;

/**
 * The class for modeling the cars in the simulation
 * 
 * @author ufmkk
 * @version 1.0
 */
public class Car implements ICar {
    
    private final int id;
    
    private final int maxSpeed;
    
    private final int acceleration;
    
    private int desiredDirection;
    
    private int positionOnStreet;
    
    private int speed;
    
    private boolean simulated;

    /**
     * Instantiates a new car with the give parameters
     * 
     * @param id the id of the car
     * @param maxSpeed the maximum speed the car can have
     * @param acceleration the acceleration the car has
     */
    public Car(int id, int maxSpeed, int acceleration) {
        this.id = id;
        this.maxSpeed = maxSpeed;
        this.acceleration = acceleration;
        this.desiredDirection = 0;
        this.speed = 0;
        this.simulated = false;
    }

    /**
     * the getter method for the id of the car
     * @return the id of the car
     */
    public int getId() {
        return id;
    }

    /**
     * the getter method for the position of the car on the street
     * @return the position of the car
     */
    public int getPosition() {
        return positionOnStreet;
    }

    /**
     * the method for updating the speed of the car
     * @param speedLimit the speed limit of the street the car is on
     */
    public void updateSpeed(int speedLimit) {
        speed = Math.min(speed + acceleration, maxSpeed);
        speed = Math.min(speed, speedLimit);
    }

    /**
     * the method for updating the desired direction of the car
     */
    public void updateDirection() {
        desiredDirection++;
        if (desiredDirection >= Main.MAX_EXIT_STREETS) {
            desiredDirection = 0;
        }
    }

    /**
     * the setter method for the speed of the car, used only in case of idling to set the speed to 0
     * @param speed the value the speed is set to
     */
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    /**
     * the getter method for the speed of the car
     * @return the speed of the car
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * the getter method for the desired direction of the car
     * @return the desired direction of the car
     */
    public int getDesiredDirection() {
        return desiredDirection;
    }

    /**
     * the setter method for the position of the car
     * @param positionOnStreet the value the position is set to
     */
    public void setPositionOnStreet(int positionOnStreet) {
        this.positionOnStreet = positionOnStreet;
    }

    /**
     * the setter method for the simulated status of the car
     * @param simulated the value the status is set to
     */
    public void setSimulated(boolean simulated) {
        this.simulated = simulated;
    }

    /**
     * the getter method for the variable simulated
     * @return if the car is simulated for this tick or not
     */
    public boolean isSimulated() {
        return simulated;
    }
}
