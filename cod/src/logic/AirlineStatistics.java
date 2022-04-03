package logic;

import data.Flight;
import data.User;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class AirlineStatistics {


    //Va returna orașul din care exista cele mai multe zboruri
    private static String findMostUsedCityAsDepartureForFlights(AirLineManager airLineManager) {
        List<Flight> allFlights = airLineManager.getAllFlights();

        Map<String, Long> cityToNumberFlightsFromCity = allFlights.stream()
                .collect(Collectors.groupingBy(flight -> flight.getFrom(), Collectors.counting()));
        var entries = cityToNumberFlightsFromCity.entrySet();

        String city = null;
        long maxNumberOfFlights = 0;
        for (Map.Entry<String, Long> entry : entries) {
            if (entry.getValue() > maxNumberOfFlights) {
                maxNumberOfFlights = entry.getValue();
                city = entry.getKey();
            }
        }
        return city;
    }

    //  Va returna userul ale cărui zboruri insumeaza cele mai multe minute (nu cel cu cele mai multe zboruri)
    private static User findUserWhoTravelTheMost(AirLineManager manager) {
        List<User> allUsers = manager.getAllUsers();
        int maxNumberOfMinutes = 0;
        User userWhoTravelTheMost = allUsers.get(0);
        // iterate over all users
        // for each user iterate over all his flights and compute the total number of minutes
        // check if the current user has traveled most minutes and update the user who travel the most
        for (User user : allUsers) {
            int nrOfMinutes = 0;
            for (Flight flight : user.getUserFlights()) {
                nrOfMinutes += flight.getDuration();
            }
            if (nrOfMinutes > maxNumberOfMinutes) {
                maxNumberOfMinutes = nrOfMinutes;
                userWhoTravelTheMost = user;
            }
        }
        return userWhoTravelTheMost;
    }

    // Întoarce lista tuturor utilizatorilor care au calatorit în orașul trimis ca parametru
    // (case insensitive - nu conteaza daca e scris cu majuscula sau nu)
    private static List<User> findAllUsersWhoTraveledToCity(AirLineManager manager, String city) {
        List<User> allUsers = manager.getAllUsers();
        List<User> userWhoTraveledToCity = new ArrayList<>();
        // iterate over all users
        // for each user iterate over all his flights and check if he flights to the given city
        // add him to userWhoTraveledToCity and return it
        for (User user : allUsers) {
            for (Flight flight : user.getUserFlights()) {
                if (flight.getTo().equalsIgnoreCase(city)) {
                    userWhoTraveledToCity.add(user);
                    break;
                }
            }
        }
        return userWhoTraveledToCity;
    }

    // Întoarce toate zborurile care au avut loc între cele doua date calendaristice
    private static List<Flight> findAllFlightsBetweenDates(AirLineManager manager, LocalDate startDate, LocalDate endDate) {
        List<Flight> allFlights = manager.getAllFlights();
        List<Flight> allFlightsBetweenDates = new ArrayList<>();
        // iterate over all flights
        // for each flight get the date and check if it's between start and end dates
        // add it to allFlightsBetweenDates list
        for (Flight flight : allFlights) {
            if (flight.getDate().isAfter(startDate) && flight.getDate().isBefore(endDate)) {
                allFlightsBetweenDates.add(flight);
            }
        }

        return allFlightsBetweenDates;
    }

    // Întoarce zborul cu durata cea mai scurta. Dacă sunt mai multe cu aceeași durată,
    // se întoarce cel cu id-ul mai mic.
    private static Flight findShortestFlight(AirLineManager manager) {
        List<Flight> allFlights = manager.getAllFlights();
        Comparator<Flight> comparator = (flight1, flight2) -> {
            if(flight1.getDuration() == flight2.getDuration()){
                return flight1.getId() - flight2.getId();
            } else {
                return flight1.getDuration() -flight2.getDuration();
            }
        };
        TreeSet<Flight> sortedFlights = new TreeSet<>(comparator);
        sortedFlights.addAll(allFlights);
        //  define a comparator which compares after duration, and if the duration is equals, then after the id
        //  define a TreeSet using that comparator
        //  add all flights to the set
        //  return first element
        return sortedFlights.first();
    }

    // Întoarce toți utilizatorii care au calatorit în acea zi.
    private static List<User> findAllUsersWhoTraveledIn(AirLineManager manager, LocalDate date) {
        List<User> allUsers = manager.getAllUsers();
        return allUsers.stream()
                .filter(user -> user.getUserFlights().stream()
                        .anyMatch(flight -> flight.getDate().getDayOfYear() == date.getDayOfYear())
                )
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        AirLineManager airLineManager = populateAirlineManager();

        //1
        String mostUsedCityAsDepartureForFlights = findMostUsedCityAsDepartureForFlights(airLineManager);
        System.out.println(mostUsedCityAsDepartureForFlights);
        // RESULT: Sibiu

        //2
        User userWhoTravelTheMost = findUserWhoTravelTheMost(airLineManager);
        System.out.println(userWhoTravelTheMost);
        // RESULT: User{email='alex@email.com', name='Alex', password='root1234'}

        //3
        List<User> allUsersWhoTraveledToMilano = findAllUsersWhoTraveledToCity(airLineManager, "Milano");
        System.out.println(allUsersWhoTraveledToMilano);
        // RESULT [User{email='alex@email.com', name='Alex', password='root1234'}, User{email='mihai@email.com', name='Mihai', password='root1234'}]

        //4
        List<Flight> allFlightsBetweenDates = findAllFlightsBetweenDates(airLineManager, LocalDate.of(2022, 2, 1), LocalDate.of(2022, 3, 1));
        System.out.println(allFlightsBetweenDates);
        // RESULT [Flight{id=1, from='Sibiu', to='Milano', date=2022-02-14, duration=120}, Flight{id=4, from='Cluj', to='Barcelona', date=2022-02-14, duration=60}]

        //5
        Flight shortestFlight = findShortestFlight(airLineManager);
        System.out.println(shortestFlight);
        // RESULT Flight{id=4, from='Cluj', to='Barcelona', date=2022-02-14, duration=60}

        //6
        List<User> allUsersWhoTraveledIn = findAllUsersWhoTraveledIn(airLineManager, LocalDate.of(2022, 1, 26));
        System.out.println(allUsersWhoTraveledIn);
        // RESULT [User{email='alex@email.com', name='Alex', password='root1234'}, User{email='mihai@email.com', name='Mihai', password='root1234'}]
    }

    private static AirLineManager populateAirlineManager() {
        Flight flight1 = new Flight(1, "Sibiu", "Milano", LocalDate.of(2022, 2, 14), 120);
        Flight flight2 = new Flight(2, "Bucuresti", "Milano", LocalDate.of(2022, 1, 26), 90);
        Flight flight3 = new Flight(3, "Sibiu", "Milano", LocalDate.of(2022, 1, 25), 85);
        Flight flight4 = new Flight(4, "Cluj", "Barcelona", LocalDate.of(2022, 2, 14), 60);
        User user1 = new User("alex@email.com", "Alex", "root1234");
        user1.addFlight(flight1);
        user1.addFlight(flight2);
        User user2 = new User("mihai@email.com", "Mihai", "root1234");
        user2.addFlight(flight2);
        user2.addFlight(flight3);
        User user3 = new User("diana@email.com", "diana", "root1234");
        user3.addFlight(flight4);

        AirLineManager airLineManager = new AirLineManager();
        airLineManager.setAllFlights(List.of(flight1, flight2, flight3, flight4));
        airLineManager.setAllUsers(List.of(user1, user2, user3));
        return airLineManager;
    }
}
