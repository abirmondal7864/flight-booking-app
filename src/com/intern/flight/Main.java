package com.intern.flight;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String FLIGHTS_CSV = "C:\\Users\\abirm\\Desktop\\Java\\Intellij Projects\\FlightBooking\\data\\flights.csv";
    private static final String BOOKINGS_CSV = "C:\\Users\\abirm\\Desktop\\Java\\Intellij Projects\\FlightBooking\\data\\bookings.csv";
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        FlightManager mgr = new FlightManager(FLIGHTS_CSV, BOOKINGS_CSV);

        while (true) {
            System.out.println("\n=== Flight Booking System ===");
            System.out.println("1. List all flights");
            System.out.println("2. Search flights (from -> to)");
            System.out.println("3. Book flight");
            System.out.println("4. Cancel booking");
            System.out.println("5. List bookings");
            System.out.println("6. Exit");
            System.out.print("Choose: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    List<Flight> allFlights = mgr.listAll();
                    allFlights.forEach(System.out::println);
                    break;
                case "2":
                    System.out.print("From: ");
                    String from = sc.nextLine().trim();
                    System.out.print("To: ");
                    String to = sc.nextLine().trim();
                    List<Flight> searchResults = mgr.search(from, to);
                    if (searchResults.isEmpty()) {
                        System.out.println("No flights found.");
                    } else {
                        searchResults.forEach(System.out::println);
                    }
                    break;
                case "3":
                    int flightId = getIntInput("Flight ID: ");
                    if (flightId == -1) break;

                    System.out.print("Passenger name: ");
                    String name = sc.nextLine().trim();

                    int seats = getIntInput("Seats to book: ");
                    if (seats == -1) break;

                    Booking booked = mgr.bookFlight(flightId, name, seats);
                    if (booked == null) {
                        System.out.println("Booking failed (invalid flight or insufficient seats).");
                    } else {
                        System.out.println("Booked: " + booked);
                    }
                    break;
                case "4":
                    int bookingId = getIntInput("Booking ID to cancel: ");
                    if (bookingId == -1) break;

                    boolean cancelled = mgr.cancelBooking(bookingId);
                    System.out.println(cancelled ? "Cancelled successfully." : "Cancel failed (not found).");
                    break;
                case "5":
                    List<Booking> allBookings = mgr.listBookings();
                    if (allBookings.isEmpty()) {
                        System.out.println("No bookings yet.");
                    } else {
                        allBookings.forEach(System.out::println);
                    }
                    break;
                case "6":
                    System.out.println("Exiting.");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        }
    }

    private static int getIntInput(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return -1;
        }
    }
}