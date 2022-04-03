package logic;

import constants.Messages;
import data.Flight;
import data.User;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AirLineManager {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/project";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private final WriterManager writer = new WriterManager();
    private List<User> allUsers = new ArrayList<>();
    private List<Flight> allFlights = new ArrayList<>();
    private User currentUser = null; // este null daca nu exista un user conectat, altfel are valoarea acelui user

    public void login(String[] commands) {
        // facem parsarea comenzii
        String email = commands[1];
        String password = commands[2];

        Optional<User> userOptional = allUsers.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();

        if (userOptional.isEmpty()) {
            writer.write(Messages.getCannotFindUser(email));
            return;
        }

        User user = userOptional.get();
        // validam parola
        if (!password.equals(user.getPassword())) {
            writer.write(Messages.getIncorrectPassword());
            return;
        }

        if (currentUser != null) {
            writer.write(Messages.getAnotherUserIsConnected());
        } else {
            currentUser = user;
            writer.write(Messages.getDisplayLoginUser(user, LocalDateTime.now()));
        }
    }

    public void signUp(String[] commands) {
        //  parsam informatiile pe care le primim
        String email = commands[1];
        String name = commands[2];
        String password = commands[3];
        String confirmationPassword = commands[4];
        //  verificam daca exista deja un user cu acest email
        for (User user : allUsers) {
            if (user.getEmail().equals(email)) {
                writer.write(Messages.getCannotAddUserEmailUsed());
                return;
            }
        }
        // validam parola
        if (password.length() < 8) {
            writer.write(Messages.getCannotAddUserPasswordTooShort());
            return;
        }

        if (!password.equals(confirmationPassword)) {
            writer.write(Messages.getCannotAddUserPasswordDiff());
            return;
        }
        // cazul bun
        User user = new User(email, name, password);
        allUsers.add(user);
        writer.write(Messages.getUserAdded(user));
    }

    public void logout(String[] commands) {
        //LOGOUT email
        if (currentUser == null) {
            return;
        }
        String email = commands[1];
        if (!currentUser.getEmail().equals(email)) {
            writer.write(Messages.getCannotLogoutUser(email));
            return;
        }
        writer.write(Messages.getLogout(email, LocalDateTime.now()));
        currentUser = null;
    }

    public void displayMyFlights() {
        if (currentUser == null) {
            writer.write(Messages.getNoConnectedUser());
        } else {
            List<Flight> userFlights = currentUser.getUserFlights();
            userFlights.forEach(flight -> writer.write(Messages.getDisplayFlight(flight)));
        }
    }

    public void addFlight(String[] commands) {
        //  1. validam daca exista curent
        //  2. verificam daca userul este deja abonat la zborul respectiv
        //  3. adaugam zborul
        int flightId = Integer.parseInt(commands[1]);
        // Verific daca este un user conectat
        if (currentUser == null) {
            writer.write(Messages.getNoConnectedUser());
            return;
        }
        // verific daca exits zborul cu id-ul primit
        Optional<Flight> flightOptional = allFlights.stream()
                .filter(flight -> flight.getId() == flightId)
                .findFirst();

        if (flightOptional.isEmpty()) {
            writer.write(Messages.getNoFlightWithId(flightId));
            return;
        }

        boolean userIsAlreadySubscribed = currentUser.getUserFlights().stream()
                .anyMatch(flight -> flight.getId() == flightId);

        if (userIsAlreadySubscribed) {
            writer.write(Messages.getUserAlreadyHasTicket(currentUser.getEmail(), flightId));
            return;
        }

        currentUser.addFlight(flightOptional.get());
        writer.write(Messages.getUserAddedFlight(currentUser.getEmail(), flightId));
    }

    public void cancelFlight(String[] commands) {
        if (currentUser == null) {
            writer.write(Messages.getNoConnectedUser());
            return;
        }

        int flightId = Integer.parseInt(commands[1]);

        // verific daca exista zborul
        boolean existsFlight = allFlights.stream().anyMatch(flight -> flight.getId() == flightId);
        if (!existsFlight) {
            writer.write(Messages.getNoFlightWithId(flightId));
            return;
        }
        // verific daca userul curent este abonat la zbor
        Optional<Flight> flightOptional = currentUser.getUserFlights().stream()
                .filter(flight -> flight.getId() == flightId)
                .findFirst();

        if (flightOptional.isEmpty()) {
            writer.write(Messages.getUserDoesNotHaveTicket(currentUser, flightId));
            return;
        }

        Flight flight = flightOptional.get();
        currentUser.deleteFlight(flight);
        writer.write(Messages.getUserCanceledTicket(currentUser, flightId));
    }

    public void addFlightDetails(String[] commands) {
        // ADD_FLIGHT_DETAILS id from to date duration
        int id = Integer.parseInt(commands[1]);
        String from = commands[2];
        String to = commands[3];
        String dateAsString = commands[4];
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateAsString, dateTimeFormatter);
        int duration = Integer.parseInt(commands[5]);

        // Verific daca exista deja un zbor cu acest id
        boolean flightWithIdAlreadyExists = allFlights.stream().anyMatch(flight -> flight.getId() == id);
        if (flightWithIdAlreadyExists) {
            writer.write(Messages.getCannotAddFlightWitId(id));
            return;
        }

        Flight flight = new Flight(id, from, to, date, duration);
        allFlights.add(flight);
        writer.write(Messages.getAddedFlight(flight));
    }

    public void deleteFlight(String[] commands) {
        // 1. scoatem din allFlights
        // 2. cautam toti userii care sunt abonati la acel zbor si ii notificam
        // 3. scoatem din listele lor
        int flightId = Integer.parseInt(commands[1]);
        Optional<Flight> flightOptional = allFlights.stream()
                .filter(flight -> flight.getId() == flightId)
                .findFirst();

        if (flightOptional.isEmpty()) {
            writer.write(Messages.getNoFlightWithId(flightId));
            return;
        }
        Flight flight = flightOptional.get();
        allFlights.remove(flight);
        writer.write(Messages.getFlightWithIdDeleted(flightId));

        // Anulez zborurile utilizatorilor abonati
        for (User user : allUsers) {
            if (user.getUserFlights().contains(flight)) {
                user.deleteFlight(flight);
                writer.write(Messages.getNotifyUserFlightWasCanceled(user, flightId));
            }
        }
    }

    public void displayAllFlights() {
        allFlights.forEach(flight -> writer.write(Messages.getDisplayFlight(flight)));
    }

    public void persistFlights() {
        //  1. realizam conexiunea la baza de date
        //  2 cream un PreparedStatement
        //  3. parcurgem lista allFights si pentru fiecare apelam metoda de insert
        String insertFlightSQL = "INSERT INTO flights VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(insertFlightSQL)) {

            for (Flight flight : allFlights) {
                preparedStatement.setInt(1, flight.getId());
                preparedStatement.setString(2, flight.getFrom());
                preparedStatement.setString(3, flight.getTo());
                preparedStatement.setDate(4, Date.valueOf(flight.getDate()));
                preparedStatement.setInt(5, flight.getDuration());

                preparedStatement.executeUpdate();
            }
            writer.write(Messages.getPersistFlights(LocalTime.now()));

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void persistUsers() {
        String insertUserSQL = "INSERT INTO users (email, name, password) VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(insertUserSQL)) {

            for (User user : allUsers) {
                preparedStatement.setString(1, user.getEmail());
                preparedStatement.setString(2, user.getName());
                preparedStatement.setString(3, user.getPassword());

                preparedStatement.executeUpdate();
            }
            writer.write(Messages.getPersistUsers(LocalTime.now()));

        } catch (SQLException exception) {
            exception.printStackTrace();
        }

    }

    public void flush() {
        writer.flush();
    }

    public void defaultCommand(String[] commands) {
        writer.write(Messages.commandNotYeImplemented());
    }

    public List<User> getAllUsers() {
        return allUsers;
    }

    public void setAllUsers(List<User> allUsers) {
        this.allUsers = allUsers;
    }

    public List<Flight> getAllFlights() {
        return allFlights;
    }

    public void setAllFlights(List<Flight> flights) {
        this.allFlights = flights;
    }
}
