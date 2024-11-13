package edu.kit.kastel.trafficsimulation.objects;

import edu.kit.kastel.trafficsimulation.Main;
import edu.kit.kastel.trafficsimulation.Simulation;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

/**
 * The class for modeling the streets in the simulation
 * 
 * @author ufmkk
 * @version 1.0
 */
public class Street implements IStreet {
    
    private boolean outsideLane;
    
    private final int length;
    
    private final int speedLimit;
    
    private final int id;
    
    private final int endNodeId;
    
    private final List<Car> cars = new ArrayList<>();
    
    private final List<Car> goneCars = new ArrayList<>();

    /**
     * Instantiates a new street with the given parameters
     * 
     * @param id the id of the street
     * @param endNodeId the id of the node the street connects to
     * @param length the length of the street
     * @param type the type of the street
     * @param speedLimit the speed limit on the street
     */
    public Street(int id, int endNodeId, int length, int type, int speedLimit) {
        this.id = id;
        this.endNodeId = endNodeId;
        this.length = length;
        if (type == 1) this.outsideLane = false;
        else if (type == 2) this.outsideLane = true;
        this.speedLimit = speedLimit;
    }

    /**
     * The method for adding cars that come from other streets
     * @param car the car that is added
     */
    public void addCar(Car car) {
        cars.add(car);
    }

    /**
     * The method for placing the cars at the start
     * @param car the added car
     * @return an error message, if one occurs; null, otherwise
     */
    public String placeCar(Car car) {
        final int carPosition = length - Main.MINIMUM_DISTANCE * cars.size();
        if (carPosition < 0) return Main.ERROR + String.format("Only %d cars can fit in to street %d", cars.size(), id);
        car.setPositionOnStreet(carPosition);
        cars.add(car);
        return null;
    }

    /**
     * The method for finding the car with the given id
     * @param id the given id
     * @return the car, if it exists; null, otherwise
     */
    public Car findCar(int id) {
        return cars.stream()
            .filter(car -> car.getId() == id)
            .findAny()
            .orElse(null);
    }

    /**
     * The getter method for the id of the street
     * @return the id of the street
     */
    public int getId() {
        return id;
    }

    /**
     * The method for updating the street
     *
     * @param simulation the {@link Simulation} the street is a part of
     */
    public void update(final Simulation simulation) {                     
        cars.sort(Collections.reverseOrder(Comparator.comparing(Car::getPosition))); 
        // sorts the cars from ahead to behind as wanted in the assignment
        for (Car car : cars) {
            if (car.isSimulated()) continue;
            car.updateSpeed(speedLimit);
            int maxPos = car.getPosition() + car.getSpeed(); // maximum distance the car can go this tick
            Car carAhead = getFirstCarAhead(car.getPosition());
            if (maxPos > length && carAhead == null) changeStreet(car, maxPos - length, simulation);
            // if the car does not reach the end of the street or has a car in front of it, then it cannot cross
            else advanceCar(car, maxPos);
        }
        cars.removeAll(goneCars);
        goneCars.clear();
    }

    /**
     * The method for resetting the simulated status of the cars
     */
    public void resetSimulated() {
        for (Car car : cars) {
            car.setSimulated(false);
        }
    }

    /**
     * the getter method for the length of the street
     * @return the length of the street
     */
    public int getLength() {
        return length;
    }

    /**
     * The method for getting the first car ahead of the given position
     * @param position the start position
     * @return the first car ahead
     */
    private Car getFirstCarAhead(int position) {
        return cars.stream()
            .filter(tempCar -> tempCar.getPosition() > position && !goneCars.contains(tempCar))
            .min(Comparator.comparing(Car::getPosition))
            .orElse(null);
    }

    /**
     * The method for checking if a car can cross streets and will be at which position after it crosses streets
     * @param car the car the method is checking for
     * @param maxPos the maximum position the car can go this tick
     * @param simulation the simulation the car is a part of
     */
    private void changeStreet(Car car, int maxPos, final Simulation simulation) {
        int tempPos = maxPos;
        Crossing endCrossing = simulation.findCrossing(endNodeId);
        if (!endCrossing.getLight(id)) { // if red light car cannot cross
            advanceCar(car, length);
            return;
        }
        Street targetStreet = simulation.findStreet(endCrossing.getExitStreet(car.getDesiredDirection()));
        Car firstCar = targetStreet.getFirstCarAhead(Main.STREET_START_POS - 1);
        if (firstCar == null) tempPos = Math.min(tempPos, targetStreet.getLength());
        // if there are no cars in the target street then the car can cross and can go until the end of the street
        // or until it reaches it's maximum position this tick
        else tempPos = Math.min(tempPos, firstCar.getPosition() - Main.MINIMUM_DISTANCE);
        // if there is a car the crossing car can go until it reaches 10 meters behind it or until it's maximum position
        if (tempPos < 0) { 
            // if there is not enough room in the target street behind the first car then the car cannot cross
            advanceCar(car, length);
            return;
        }
        // the car can cross
        if (car.getPosition() == length && tempPos == 0) car.setSpeed(Main.STOP_SPEED);
        // if the car only crossed and has not moved in either streets, that means it is idling and should have 0 speed
        car.updateDirection();
        car.setSimulated(true); // set the simulates status to true so that the car is nt advanced again this tick
        car.setPositionOnStreet(tempPos);
        targetStreet.addCar(car);
        goneCars.add(car);
    }

    /**
     * The method for advancing the car on the street if it cannot cross to the other street
     * @param car the car the method is checking for
     * @param maxPos the maximum position the car can go this tick
     */
    private void advanceCar(Car car, int maxPos) {
        int tempPos = Math.min(length, maxPos);
        boolean overtake = true;
        Car carAhead = getFirstCarAhead(car.getPosition());
        if (carAhead == null) { 
            // if there are no cars ahead, the car can go until the end of the street or until it's maximum position
            if (tempPos == car.getPosition()) car.setSpeed(Main.STOP_SPEED);
            car.setPositionOnStreet(tempPos);
            return;
        }
        if (!outsideLane) overtake = false; // if there are no outside lanes, the car cannot overtake
        int firstCarAheadPos = carAhead.getPosition();
        if (tempPos - firstCarAheadPos < Main.MINIMUM_DISTANCE) overtake = false;
        // if the car cannot go far enough the first car ahead, then it cannot overtake
        Car secondCarAhead = getFirstCarAhead(firstCarAheadPos);
        int secondCarAheadPos;
        if (secondCarAhead == null) secondCarAheadPos = length + Main.MINIMUM_DISTANCE;
        // if there are only one car ahead the second car is basically length + 10m so that the end of the street
        // is the last valid place for the car being advanced
        else secondCarAheadPos = secondCarAhead.getPosition();
        if (secondCarAheadPos - firstCarAheadPos < Main.MINIMUM_DISTANCE * 2) overtake = false;
        // if there are not enough distance between the cars ahead or 
        // after the first car until the end of the street the car being advanced cannot overtake
        if (!overtake) {
            tempPos = Math.min(maxPos, firstCarAheadPos - Main.MINIMUM_DISTANCE);
            // if the car cannot overtake it can go until it's maximum position or 10m behind the first car ahead
        }
        else {
            tempPos = Math.min(maxPos, secondCarAheadPos - Main.MINIMUM_DISTANCE);
            // if the car can overtake it can go until it's maximum position or 10m behind the second car ahead
        }
        if (tempPos == car.getPosition()) car.setSpeed(Main.STOP_SPEED);
        car.setPositionOnStreet(tempPos);
    }
}
