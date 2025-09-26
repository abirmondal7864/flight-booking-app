package com.intern.flight;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FlightManager {
    private List<Flight> flights;
    private String flightsFile;
    private String bookingsFile;
    private AtomicInteger nextBookingId;

    public FlightManager(String flightsFile, String bookingsFile) {
        this.flightsFile = flightsFile;
        this.bookingsFile = bookingsFile;
        this.flights = CSVUtil.readFlights(flightsFile);
        this.nextBookingId = new AtomicInteger(determineNextBookingId());
    }

    private int determineNextBookingId() {
        List<Booking> bookings = CSVUtil.readBookings(bookingsFile);
        int max = 0;
        for (Booking b : bookings) {
            max = Math.max(max, b.getBookingId());
        }
        return max + 1;
    }

    public List<Flight> listAll() {
        return new ArrayList<>(flights);
    }

    public List<Flight> search(String from, String to) {
        String f = from.trim().toLowerCase();
        String t = to.trim().toLowerCase();
        List<Flight> out = new ArrayList<>();
        for (Flight fl : flights) {
            if (fl.getFrom().toLowerCase().contains(f) && fl.getTo().toLowerCase().contains(t)) {
                out.add(fl);
            }
        }
        return out;
    }

    public Flight getFlightById(int id) {
        return flights.stream().filter(f -> f.getId() == id).findFirst().orElse(null);
    }

    public Booking bookFlight(int flightId, String passengerName, int seats) {
        Flight fl = getFlightById(flightId);
        if (fl == null || fl.getAvailableSeats() < seats) {
            return null;
        }
        double amount = seats * fl.getPrice();
        int bookingId = nextBookingId.getAndIncrement();
        String bookingDate = LocalDate.now().toString();
        Booking b = new Booking(bookingId, flightId, passengerName, seats, amount, bookingDate);
        // reduce seats
        fl.setAvailableSeats(fl.getAvailableSeats() - seats);
        // persist
        CSVUtil.writeFlights(flightsFile, flights);
        CSVUtil.appendBooking(bookingsFile, b.toCSV());
        return b;
    }

    public boolean cancelBooking(int bookingId) {
        List<String> lines = CSVUtil.readAllLines(bookingsFile);
        List<String> newLines = new ArrayList<>();
        boolean found = false;
        int flightId = -1;
        int seats = 0;
        for (String l : lines) {
            if (l.trim().isEmpty() || l.startsWith("#")) {
                newLines.add(l);
                continue;
            }
            List<String> cols = CSVUtil.parseCSVLine(l);
            if (cols.size() < 6) {
                newLines.add(l);
                continue;
            }
            try {
                int id = Integer.parseInt(cols.get(0));
                if (id == bookingId) {
                    found = true;
                    flightId = Integer.parseInt(cols.get(1));
                    seats = Integer.parseInt(cols.get(3));
                } else {
                    newLines.add(l);
                }
            } catch (Exception e) {
                newLines.add(l);
            }
        }
        if (found) {
            try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(bookingsFile))) {
                for (String l : newLines) pw.println(l);
            } catch (java.io.IOException e) {
                System.err.println("Error updating bookings: " + e.getMessage());
                return false;
            }
            Flight fl = getFlightById(flightId);
            if (fl != null) {
                fl.setAvailableSeats(fl.getAvailableSeats() + seats);
                CSVUtil.writeFlights(flightsFile, flights);
            }
            return true;
        }
        return false;
    }

    public List<Booking> listBookings() {
        return CSVUtil.readBookings(bookingsFile);
    }
}