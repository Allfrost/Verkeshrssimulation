package edu.kit.kastel.trafficsimulation;

import edu.kit.kastel.trafficsimulation.io.SimulationFileLoader;
import edu.kit.kastel.trafficsimulation.objects.Car;
import edu.kit.kastel.trafficsimulation.objects.Crossing;
import edu.kit.kastel.trafficsimulation.objects.Street;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the simulation as described in the assignment
 * 
 * @author ufmkk
 * @version 1.0
 */
public class Simulation implements ISimulation {
    
    private boolean running;
    
    private boolean loaded;
    
    private final List<Street> streets = new ArrayList<>();
    
    private final List<Crossing> crossings = new ArrayList<>();

    private final List<Crossing> tempCrossings = new ArrayList<>();
    
    private final List<Street> tempStreets = new ArrayList<>();

    /**
     * Instantiates a new simulation
     */
    Simulation() {
        this.running = true;
        this.loaded = false;
    }

    /**
     * The method for checking if the simulation is running or not
     * @return true, if the simulation is running; false, otherwise
     */
    public boolean isRunning() {
        return this.running;
    }

    /**
     * The method for quitting the simulation
     */
    public void quit() {
        this.running = false;
    }

    /**
     * The method for loading the data int the give file in to the simulation
     * 
     * @param file the path to the folder
     * @return An error message, if an error occurs; Ready, otherwise
     * @throws IOException  if the file does not exist or points to a directory.
     */
    public String load(String file) throws IOException {
        resetTemp();
        SimulationFileLoader fileLoader;
        String output;
        try {
            fileLoader = new SimulationFileLoader(file); 
        }   catch (IOException ioException) {
            return Main.ERROR + ioException;
        }
        output = createCrossings(fileLoader.loadCrossings()); // checks if the given crossings are in correct format
        if (output != null) return output;
        
        output = createStreets(fileLoader.loadStreets()); // checks if the given streets are in correct format
        if (output != null) return output;
        
        output = createCars(fileLoader.loadCars()); // checks if the given cars are in correct format
        if (output != null) return output;
        
        output = checkValidity(); // checks if the road network is valid
        if (output != null) return output;
        
        loaded = true;
        reset();
        this.crossings.addAll(tempCrossings);
        this.streets.addAll(tempStreets);
        resetTemp();
        
        return Main.READY;
    }

    /**
     * The method for finding the crossing with the given id
     * 
     * @param id the given id
     * @return the crossing with the given id, if it does exist; null, if it does not
     */
    public Crossing findCrossing(int id) {
        return crossings.stream()
            .filter(crossing -> crossing.getId() == id)
            .findAny()
            .orElse(null);
    }

    /**
     * The method for finding the crossing with the given id used only on load operations
     *
     * @param id the given id
     * @return the crossing with the given id, if it does exist; null, if it does not
     */
    public Crossing findNewCrossing(int id) {
        return tempCrossings.stream()
            .filter(crossing -> crossing.getId() == id)
            .findAny()
            .orElse(null);
    }

    /**
     * The method for finding the street with the given id
     *
     * @param id the given id
     * @return the street with the given id, if it does exist; null, if it does not
     */
    public Street findStreet(int id) {
        return streets.stream()
            .filter(street -> street.getId() == id)
            .findAny()
            .orElse(null);
    }

    /**
     * The method for finding the street with the given id only used on load
     *
     * @param id the given id
     * @return the street with the given id, if it does exist; null, if it does not
     */
    public Street findNewStreet(int id) {
        return tempStreets.stream()
            .filter(street -> street.getId() == id)
            .findAny()
            .orElse(null);
    }

    /**
     * The method for finding the car with the given id
     *
     * @param id the given id
     * @return the car with the given id, if it does exist; null, if it does not
     */
    public Car findCar(int id) {
        Street tempStreet = streets.stream()
            .filter(street -> street.findCar(id) != null)
            .findAny()
            .orElse(null);
        if (tempStreet == null) return null;
        return tempStreet.findCar(id);
    }

    /**
     * The method for finding the car with the given id only used on load
     *
     * @param id the given id
     * @return the car with the given id, if it does exist; null, if it does not
     */
    public Car findNewCar(int id) {
        Street tempStreet = tempStreets.stream()
            .filter(street -> street.findCar(id) != null)
            .findAny()
            .orElse(null);
        if (tempStreet == null) return null;
        return tempStreet.findCar(id);
    }

    /**
     * The method for finding the street the car with the given id is on
     * 
     * @param id the given id
     * @return the street the car is on, if the car exists; null, otherwise 
     */
    public Street findStreetWithCar(int id) {
        return streets.stream()
            .filter(street -> street.findCar(id) != null)
            .findFirst()
            .orElse(null);
    }

    /**
     * The method fot getting the position of the cr with the given id
     * 
     * @param carId the given car id
     * @return the position, street and the speed of the car in the correct format
     */
    public String giveCarPosition(int carId) {
        
        Car tempCar = findCar(carId);
        if (tempCar == null) return Main.ERROR + "No such car exists";
        
        int streetId = findStreetWithCar(carId).getId();
        int speed = tempCar.getSpeed();
        int position = tempCar.getPosition();

        return
            String.format("Car %d on street %d with speed %d and position %d", carId, streetId, speed, position);
    }

    /**
     * The method for advancing the simulation for the given ticks
     * 
     * @param ticks How many ticks the simulation will be advanced
     * @return An error message, if an error occurs; Ready, otherwise
     */
    public String update(int ticks) {
        if (!loaded) return Main.ERROR + "Nothing is loaded right now";
        final StringBuilder output = new StringBuilder();
        for (int i = 0; i < ticks; i++) {
            for (Street street : streets) {
                street.update(this); // updates every street from the lowest to the highest id
            }
            for (Street street : streets) street.resetSimulated(); // resets the simulated status of every car
            for (Crossing crossing : crossings) crossing.update();
            // updates every crossing from the lowest to the highest id
        }
        if (output.isEmpty()) output.append("READY"); // if no error then print READY
        return output.toString();
    }

    /**
     * The method for resetting the simulation
     */
    private void reset() {
        crossings.clear();
        streets.clear();
    }

    /**
     * The method for resetting the temporary crossings/streets used for checking validity before loading
     */
    private void resetTemp() {
        tempCrossings.clear();
        tempStreets.clear();
    }

    /**
     * The method for translating the Strings given for crossings to objects
     * @param crossings the string list of crossings
     * @return an error message if they are invalid, null otherwise
     */
    private String createCrossings(List<String> crossings) {
        for (String crossing : crossings) {
            int indexFirstParameterEnd = crossing.indexOf(":"); //slices the string in to their integer parts
            if (indexFirstParameterEnd == -1) return Main.ERROR + "Illegal input format";
            int indexSecondParameterEnd = crossing.indexOf("t");
            if (indexSecondParameterEnd == -1) return Main.ERROR + "Illegal input format";
            final int id;
            try {
                id = Integer.parseInt(crossing.substring(Main.FIRST_PARAMETER_INDEX, indexFirstParameterEnd));
            }   catch (IllegalArgumentException illegalArgumentException) {
                return Main.ERROR + "Id has to be a number";
            }
            final int duration;
            try {
                duration = Integer.parseInt(crossing.substring(indexFirstParameterEnd + 1, indexSecondParameterEnd));
            }   catch (IllegalArgumentException illegalArgumentException) {
                return Main.ERROR + "Duration of the green light has to be a number";
            }
            if (id < 0) {
                return Main.ERROR + "Id cannot be negative";
            }
            Crossing controlCrossing = findNewCrossing(id);
            if (controlCrossing != null) return Main.ERROR + "A crossing with the given id already exists";
            
            if (duration < 3 && duration != 0 || duration > Main.MAX_GREEN_LIGHT_DURATION) {
                return Main.ERROR + "Green light duration cannot be less than 3 or greater than 10";
            }
            Crossing tempCrossing = new Crossing(id, duration);
            tempCrossings.add(tempCrossing);
        }
        return null;
    }

    /**
     * The method for translating the Strings given for streets to objects
     * @param streets the string list of streets
     * @return an error message if they are invalid, null otherwise
     */
    private String createStreets(List<String> streets) {
        for (int id = 0; id < streets.size(); id++) {
            String tempStreet = streets.get(id);
            int indexFirstParamEnd = tempStreet.indexOf("-"); //slices the input string so that every parameter stands
            if (indexFirstParamEnd == -1) return Main.ERROR + "Illegal input format";
            final int startNodeId;                            // alone as an integer
            try {
                startNodeId = Integer.parseInt(tempStreet.substring(Main.FIRST_PARAMETER_INDEX, indexFirstParamEnd));
            }   catch (IllegalArgumentException illegalArgumentException) {
                return Main.ERROR + "Illegal input format";
            }
            
            Crossing startNode = findNewCrossing(startNodeId);
            if (startNode == null) return Main.ERROR + "The street cannot begin from a non-existent crossing";
            startNode.addExitStreet(id);

            int indexSecondParamEnd = tempStreet.indexOf(":");
            if (indexSecondParamEnd == -1) return Main.ERROR + "Illegal input format";
            final int endNodeId;
            try {
                endNodeId = Integer.parseInt(tempStreet.substring(indexFirstParamEnd + 3, indexSecondParamEnd));
            }   catch (IllegalArgumentException illegalArgumentException) {
                return Main.ERROR + "Illegal input format";
            }
            
            if (endNodeId == startNodeId) return Main.ERROR + "A street cannot start and end at the same crossing";
            
            Crossing endNode = findNewCrossing(endNodeId);
            if (endNode == null) return Main.ERROR + "The street cannot connect to a non-existent crossing";
            endNode.addEnterStreet(id);

            int indexThirdParamEnd = tempStreet.indexOf(",");
            if (indexThirdParamEnd == -1) return Main.ERROR + "Illegal input format";
            final int length;
            try {
                length = Integer.parseInt(tempStreet.substring(indexSecondParamEnd + 1, indexThirdParamEnd - 1));
            }   catch (IllegalArgumentException illegalArgumentException) {
                return Main.ERROR + "Illegal input format";
            }
            if (length < Main.MINIMUM_STREET_LENGTH || length > Main.MAXIMUM_STREET_LENGTH) {
                return Main.ERROR + "The length of a street cannot be greater than 10000 or less than 10";
            }

            int indexFourthParamEnd = tempStreet.indexOf("x");
            if (indexFourthParamEnd == -1) return Main.ERROR + "Illegal input format";
            final int type;
            try {
                type = Integer.parseInt(tempStreet.substring(indexThirdParamEnd + 1, indexFourthParamEnd));
            }   catch (IllegalArgumentException illegalArgumentException) {
                return Main.ERROR + "Illegal input format";
            }
            if (type != 1 && type != 2) return Main.ERROR + "Type of a street has to be either 1 or 2";
            
            int indexFifthParamEnd = tempStreet.indexOf("max");
            if (indexFifthParamEnd == -1) return Main.ERROR + "Illegal input format";
            final int speedLimit;
            try {
                speedLimit = Integer.parseInt(tempStreet.substring(indexFourthParamEnd + 2, indexFifthParamEnd));
            }   catch (IllegalArgumentException illegalArgumentException) {
                return Main.ERROR + "Illegal input format";
            }
            if (speedLimit < 5 || speedLimit > Main.MAXIMUM_SPEED_LIMIT) {
                return Main.ERROR + "Speed limit of a street cannot be greater than 40 or less than 5";
            }

            Street street = new Street(id, endNodeId, length, type, speedLimit);
            this.tempStreets.add(street);
        }
        return null;
    }

    /**
     * The method for translating the Strings given for cars to objects
     * @param cars the string list of crossings
     * @return an error message if they are invalid, null otherwise
     */
    private String createCars(List<String> cars) {
        for (String car : cars) {
            final String output;
            String carString = car;
            int indexFirstParamEnd = carString.indexOf(",");  //slices the input string so that every parameter stands
            if (indexFirstParamEnd == -1) return Main.ERROR + "Illegal input format"; // alone as an integer
            carString = carString.replaceFirst(",", " "); 
            int indexSecondParamEnd = carString.indexOf(",");
            if (indexSecondParamEnd == -1) return Main.ERROR + "Illegal input format";
            carString = carString.replaceFirst(",", " ");
            int indexThirdParamEnd = carString.indexOf(",");
            if (indexThirdParamEnd == -1) return Main.ERROR + "Illegal input format";

            final int id;
            final int streetId;
            final int maxSpeed;
            final int acceleration;
            
            try {
                id = Integer.parseInt(carString.substring(Main.FIRST_PARAMETER_INDEX, indexFirstParamEnd));
                streetId = Integer.parseInt(carString.substring(indexFirstParamEnd + 1, indexSecondParamEnd));
                maxSpeed = Integer.parseInt(carString.substring(indexSecondParamEnd + 1, indexThirdParamEnd));
                acceleration = Integer.parseInt(carString.substring(indexThirdParamEnd + 1));
            }   catch (IllegalArgumentException illegalArgumentException) {
                return Main.ERROR + "Illegal input format";
            }
            if (id < 0) return Main.ERROR + "The id cannot be negative";
            Car controlCar = findNewCar(id);
            if (controlCar != null) return Main.ERROR + "A car with the given id already exists";
                
            Street tempStreet = findNewStreet(streetId);
            if (tempStreet == null) return Main.ERROR + "The car cannot start on a non-existent street";
            if (maxSpeed < Main.MINIMUM_HIGHEST_SPEED || maxSpeed > Main.MAXIMUM_SPEED_LIMIT) {
                return Main.ERROR + "The max speed of a car has to be between 20 and 40";
            }
            if (acceleration < 1 || acceleration > Main.MAXIMUM_ACCELERATION) {
                return Main.ERROR + "The acceleration of a car has to be between 1 and 10";
            }

            Car tempCar = new Car(id, maxSpeed, acceleration);
            output = findNewStreet(streetId).placeCar(tempCar);
            if (output != null) return output;
        }
        return null;
    }

    /**
     * The method for checking if each crossing is connected and if any crossing 
     * has more than 4 enter/exit streets connected to it
     * @return an error message if they are invalid, null otherwise
     */
    private String checkValidity() {
        for (Crossing crossing : this.tempCrossings) {
            if (crossing.getNumberOfExitStreets() < 1 || crossing.getNumberOfIncomingStreets() < 1) {
                return Main.ERROR + "Each node has to have at least 1 entering and 1 exiting street";
            }
            if (crossing.getNumberOfIncomingStreets() > Main.MAX_STREET_CONNECTIONS 
                || crossing.getNumberOfExitStreets() > Main.MAX_STREET_CONNECTIONS) {
                return Main.ERROR + "Crossings can have a maximum of 4 entering or exiting streets";
            }
        }
        return null;
    }
    
}
