package edu.kit.kastel.trafficsimulation.objects;

import edu.kit.kastel.trafficsimulation.Main;

import java.util.Arrays;

/**
 * The class for modeling the crossings in the simulation
 * 
 * @author ufmkk
 * @version 1.0
 */
public class Crossing implements ICrossing {
    
    private final int id;
    
    private final boolean trafficLight;

    private final int greenLightDuration;
    
    private int greenLightPhase;
    
    private int remainingDuration;
    
    private int numberOfIncomingStreets;
    
    private int numberOfExitStreets;
    
    private final int[] outgoingStreetIds = new int[Main.MAX_STREET_CONNECTIONS];
    
    private final int[] incomingStreetIds = new int[Main.MAX_STREET_CONNECTIONS];

    /**
     * Instantiates a new crossing with the given parameters
     * @param id the id of the crossing
     * @param greenLightDuration the duration of the green light, 0 means there is no light
     */
    public Crossing(int id, int greenLightDuration) {
        this.id = id;
        this.greenLightDuration = greenLightDuration;
        if (greenLightDuration > 0) {
            this.trafficLight = true;
            this.greenLightPhase = 0;
        }
        else {
            this.trafficLight = false;
        }
        
        remainingDuration = greenLightDuration;
        
        numberOfIncomingStreets = 0;
        numberOfExitStreets = 0;

        Arrays.fill(outgoingStreetIds, -1);
        Arrays.fill(incomingStreetIds, -1);
    }

    /**
     * the method for adding a new exiting street
     * @param id the id of the exiting street
     */
    public void addExitStreet(int id) {
        numberOfExitStreets++;
        for (int i = 0; i < outgoingStreetIds.length; i++) {
            if (outgoingStreetIds[i] == -1) {
                outgoingStreetIds[i] = id;
                break;
            }
        }
    }

    /**
     * the method for adding a new entering street
     * @param id the id of the entering street
     */
    public void addEnterStreet(int id) {
        numberOfIncomingStreets++;
        for (int i = 0; i < incomingStreetIds.length; i++) {
            if (incomingStreetIds[i] == -1) {
                incomingStreetIds[i] = id;
                break;
            }
        }
    }

    /**
     * the method for getting the if of the exit street a car exits on
     * @param direction the desired direction of the car
     * @return the id of the street the car exits on
     */
    public int getExitStreet(int direction) {
        if (outgoingStreetIds[direction] == -1) {
            return outgoingStreetIds[Main.FIRST_PARAMETER_INDEX];
        }
        return outgoingStreetIds[direction];
    }

    /**
     * the method for determining if the cars on the street with the given id can cross or not
     * @param incomingStreetId the id of the street that is checked for
     * @return true, if the cars can cross; false, otherwise
     */
    public boolean getLight(int incomingStreetId) {
        if (!trafficLight) return true;
        else {
            return incomingStreetIds[greenLightPhase] == incomingStreetId;
        }
    }

    /**
     * the getter method for the id of the crossing
     * @return the id of the crossing
     */
    public int getId() {
        return id;
    }

    /**
     * the getter method for the number of exit streets of the crossing
     * @return the number of the exit streets of the crossing
     */
    public int getNumberOfExitStreets() {
        return numberOfExitStreets;
    }

    /**
     * the getter method for the number of enter streets of the crossing
     * @return the number of the entering streets of the crossing
     */
    public int getNumberOfIncomingStreets() {
        return numberOfIncomingStreets;
    }

    /**
     * the method for updating the crossing
     */
    public void update() {
        if (greenLightDuration == 0) return; // if there is no light, also no need to update
        remainingDuration--;
        if (remainingDuration <= 0) { // if duration is 0 then change the street the green light is shining for
            remainingDuration = greenLightDuration;
            greenLightPhase++;
            if (greenLightPhase >= numberOfIncomingStreets) { // if it was on the fourth go back to first
                greenLightPhase = 0;
            }
        }
    }
}
