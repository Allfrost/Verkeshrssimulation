package edu.kit.kastel.trafficsimulation;

import java.io.IOException;
import java.util.Scanner;

/**
 * Main class for the second final assignment. Contains the entry point
 * and input/output constants.
 * 
 * @author ufmkk
 * @version 1.0
 */
public final class Main {

    /**
     * The start of an output string for a failed operation
     */
    public static final String ERROR = "Error: ";

    /**
     * The separator between commands
     */
    public static final String COMMAND_SEPARATOR = " ";

    /**
     * Index of the first Parameter
     */
    public static final int FIRST_PARAMETER_INDEX = 0;

    /**
     * The offset number required to separate the number and the command load
     */
    public static final int LOAD_SUBSTRING_OFFSET = 5;

    /**
     * The offset number required to separate the number and the commands simulate and position
     */
    public static final int SIM_POS_SUBSTRING_OFFSET = 9;

    /**
     * The minimum distance two cars have to hold between
     */
    public static final int MINIMUM_DISTANCE = 10;

    /**
     * The start position of a street
     */
    public static final int STREET_START_POS = 0;

    /**
     * The speed the cars have when they are idle
     */
    public static final int STOP_SPEED = 0;

    /**
     * The number of maximum exit streets a crossing can have
     */
    public static final int MAX_EXIT_STREETS = 4;

    /**
     * The output string for successful load or simulate operations 
     */
    public static final String READY = "READY";

    /**
     * Exception message in case of utility class instantiation
     */
    public static final String UTILITY_CLASS_INSTANTIATION = "Utility classes cannot be instantiated";

    /**
     * The maximum number of entering or exiting streets a crossing can have
     */
    public static final int MAX_STREET_CONNECTIONS = 4;

    /**
     * The maximum duration a green light can have
     */
    public static final int MAX_GREEN_LIGHT_DURATION = 10;

    /**
     * The minimum length a street can have
     */
    public static final int MINIMUM_STREET_LENGTH = 10;

    /**
     * The maximum length a street can have
     */
    public static final int MAXIMUM_STREET_LENGTH = 10000;

    /**
     * The maximum speed limit a car or a street can have
     */
    public static final int MAXIMUM_SPEED_LIMIT = 40;

    /**
     * The minimum highest speed a car can have
     */
    public static final int MINIMUM_HIGHEST_SPEED = 20;

    /**
     * The maximum acceleration a car can have
     */
    public static final int MAXIMUM_ACCELERATION = 10;

    /**
     * Private constructor to avoid object generation
     */
    private Main() {
        throw new IllegalStateException(Main.UTILITY_CLASS_INSTANTIATION);
    }

    /**
     * Entry point to the program. Checks the given input and produces corresponding
     * output.
     * @param args should be empty
     * @throws IOException if the load command is called for a non-existent or an empty file
     */
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        Simulation simulation = new Simulation();
        
        while (simulation.isRunning()) {
            final String input = scanner.nextLine();
            final String output = Command.executeCommand(input, simulation);
            if (output != null) System.out.println(output);
        }
    }
}
