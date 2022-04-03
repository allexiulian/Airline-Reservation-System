package data;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int id;
    private String email;
    private String name;
    private String password;
    private List<Flight> userFlights = new ArrayList<>();

    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public List<Flight> getUserFlights() {
        return userFlights;
    }

    public void addFlight(Flight flight){
        userFlights.add(flight);
    }

    public void deleteFlight(Flight flight){
        userFlights.remove(flight);
    }


    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
