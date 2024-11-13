package edu.kit.kastel.trafficsimulation;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * List of available commands with their command line interaction expressions.
 * This class is heavily inspired by the solution of Blatt 5 Aufgabe A
 * 
 * @author ufmkk
 * @version 1.0
 */
public enum Command {

    /**
     * Loads the data in the given file
     */
    LOAD("load" + Main.COMMAND_SEPARATOR + "[^ ]+") {    
        @Override
        String execute(Matcher input, final Simulation simulation) throws IOException {
            return simulation.load(input.group(Main.FIRST_PARAMETER_INDEX).substring(Main.LOAD_SUBSTRING_OFFSET));
        }
    },

    /**
     * Advances the simulation for a length of given ticks
     */
    SIMULATE("simulate" + Main.COMMAND_SEPARATOR + "\\d+") {
        @Override
        String execute(Matcher input, final Simulation simulation) {
            int ticks;
            try {
                ticks = Integer
                    .parseInt(input.group(Main.FIRST_PARAMETER_INDEX).substring(Main.SIM_POS_SUBSTRING_OFFSET));
            }   catch (IllegalArgumentException illegalArgumentException) {
                return Main.ERROR + "The number of ticks has to be a number.";
            }
            if (ticks < 0) {
                return Main.ERROR + "The number of ticks cannot be negative.";
            }
            return simulation.update(ticks);
        }
    },

    /**
     * Gives the position, the street the car is on, and it's speed for the car with the given id
     */
    POSITION("position" + Main.COMMAND_SEPARATOR + "\\d+") {
        @Override
        String execute(Matcher input, final Simulation simulation) {
            int carId;
            try {
                carId = Integer
                    .parseInt(input.group(Main.FIRST_PARAMETER_INDEX).substring(Main.SIM_POS_SUBSTRING_OFFSET));
            }   catch (IllegalArgumentException illegalArgumentException) {
                return Main.ERROR + "The car id has to be a number.";
            }
            if (carId < 0) {
                return Main.ERROR + "The ids of cars start from 0 and therefore cannot be negative.";
            }
            return simulation.giveCarPosition(carId); 
        }
    },

    /**
     * Quits the simulation
     */
    QUIT("quit") {
        @Override
        String execute(Matcher input, final Simulation simulation) {
            simulation.quit();
            return null;
        }
    };

    /**
     * The error message if the given command is not found
     */
    public static final String COMMAND_NOT_FOUND = Main.ERROR + "Command not found!";
    
    private final Pattern pattern;

    /**
     * Instantiates a new command with the given String. The given String must be a
     * compilable {@link Pattern}.
     *
     * @param pattern the pattern of this command
     */
    Command(final String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    /**
     * Executes the command contained in the input if there is any, returns an error
     * message otherwise. If a command is found in the input, returns the result of
     * this input performed on the simulation
     *
     * @param input the line of input
     * @param simulation the {@link Simulation} the command is executed on
     *
     * @return the result of the command execution, may contain error messages or be
     *         null if there is no output
     */
    public static String executeCommand(final String input, final Simulation simulation) throws IOException {
        for (final Command command : Command.values()) {
            final Matcher matcher = command.pattern.matcher(input);
            if (matcher.matches()) {
                return command.execute(matcher, simulation);
            }
        }
        return COMMAND_NOT_FOUND;
    }

    /**
     * Executes the given input on the given simulation
     *
     * @param input the line of input
     * @param simulation the simulation the command is executed on
     *
     * @return the result of the command execution, may contain error messages or be
     *         null if there is no output
     */
    abstract String execute(Matcher input, Simulation simulation) throws IOException;
    
}
