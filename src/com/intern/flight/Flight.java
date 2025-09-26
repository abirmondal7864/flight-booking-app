package com.intern.flight;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Flight {
    private int id;
    private String airline;
    private String from;
    private String to;
    private String departure; // ISO datetime string
    private String arrival;
    private int totalSeats;
    private int availableSeats;
    private double price;

    public Flight(int id, String airline, String from, String to, String departure, String arrival,
                  int totalSeats, int availableSeats, double price) {
        this.id = id;
        this.airline = airline;
        this.from = from;
        this.to = to;
        this.departure = departure;
        this.arrival = arrival;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.price = price;
    }

    public int getId() { return id; }
    public String getAirline() { return airline; }
    public String getFrom() { return from; }
    public String getTo() { return to; }
    public String getDeparture() { return departure; }
    public String getArrival() { return arrival; }
    public int getTotalSeats() { return totalSeats; }
    public int getAvailableSeats() { return availableSeats; }
    public double getPrice() { return price; }

    public void setAvailableSeats(int seats) { this.availableSeats = seats; }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
        String depFmt = formatDateTime(departure, fmt);
        String arrFmt = formatDateTime(arrival, fmt);

        return String.format(
                "%d | %s : %s -> %s | dep: %s | arr: %s | price: %.2f | avail: %d",
                id, airline, from, to, depFmt, arrFmt, price, availableSeats
        );
    }

    private String formatDateTime(String iso, DateTimeFormatter fmt) {
        try {
            return LocalDateTime.parse(iso).format(fmt);
        } catch (Exception e) {
            return iso; // fallback if parsing fails
        }
    }


    public String toCSV() {
        return String.format("%d,%s,%s,%s,%s,%s,%d,%d,%.2f",
                id, escapeCSV(airline), escapeCSV(from), escapeCSV(to), departure, arrival, totalSeats, availableSeats, price);
    }

    private String escapeCSV(String s) {
        if (s.contains(",") || s.contains("\"")) {
            s = s.replace("\"", "\"\"");
            return "\"" + s + "\"";
        }
        return s;
    }
}
