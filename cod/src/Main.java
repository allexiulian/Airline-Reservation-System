import constants.Commands;
import logic.AirLineManager;
import logic.ReaderManager;

public class Main {

    public static void main(String[] args) {
        // write your code here
        AirLineManager airLineManager = new AirLineManager();

        ReaderManager reader = new ReaderManager();
        String line = reader.readLine();


        while (line != null) {
            String[] commands = line.split(" ");
            Commands command;
            try {
                command = Commands.valueOf(commands[0]);
            } catch (IllegalArgumentException e) {
                command = Commands.DEFAULT_COMMAND;
            }

            switch (command) {
                case SIGNUP: {
                    airLineManager.signUp(commands);
                    break;
                }
                case LOGIN: {
                    airLineManager.login(commands);
                    break;
                }
                case LOGOUT: {
                    airLineManager.logout(commands);
                    break;
                }
                case DISPLAY_MY_FLIGHTS: {
                    airLineManager.displayMyFlights();
                    break;
                }
                case ADD_FLIGHT: {
                    airLineManager.addFlight(commands);
                    break;
                }
                case CANCEL_FLIGHT: {
                    airLineManager.cancelFlight(commands);
                    break;
                }
                case ADD_FLIGHT_DETAILS: {
                    airLineManager.addFlightDetails(commands);
                    break;
                }
                case DELETE_FLIGHT: {
                    airLineManager.deleteFlight(commands);
                    break;
                }
                case DISPLAY_FLIGHTS: {
                    airLineManager.displayAllFlights();
                    break;
                }
                case PERSIST_FLIGHTS: {
                    airLineManager.persistFlights();
                    break;
                }
                case PERSIST_USERS: {
                    airLineManager.persistUsers();
                    break;
                }
                // celelalte cazuri
                case DEFAULT_COMMAND: {
                    airLineManager.defaultCommand(commands);
                    break;
                }
            }
            // trec la urmatoare linie
            line = reader.readLine();
        }

        // dupa while
        airLineManager.flush();
    }

}
