package com.intern.flight;

public class Booking {
    private int bookingId;
    private int flightId;
    private String passengerName;
    private int seatsBooked;
    private double amountPaid;
    private String bookingDate;

    public Booking(int bookingId, int flightId, String passengerName, int seatsBooked, double amountPaid, String bookingDate) {
        this.bookingId = bookingId;
        this.flightId = flightId;
        this.passengerName = passengerName;
        this.seatsBooked = seatsBooked;
        this.amountPaid = amountPaid;
        this.bookingDate = bookingDate;
    }

    public int getBookingId() { return bookingId; }
    public int getFlightId() { return flightId; }
    public String getPassengerName() { return passengerName; }

    public String toCSV() {
        return String.format("%d,%d,%s,%d,%.2f,%s",
                bookingId, flightId, escapeCSV(passengerName), seatsBooked, amountPaid, bookingDate);
    }

    private String escapeCSV(String s) {
        if (s.contains(",") || s.contains("\"")) {
            s = s.replace("\"", "\"\"");
            return "\"" + s + "\"";
        }
        return s;
    }

    @Override
    public String toString() {
        return String.format("Booking %d | Flight %d | %s | seats:%d | paid:%.2f | date:%s",
                bookingId, flightId, passengerName, seatsBooked, amountPaid, bookingDate);
    }
}
