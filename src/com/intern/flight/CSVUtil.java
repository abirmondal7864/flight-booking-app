package com.intern.flight;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVUtil {
    public static List<Flight> readFlights(String filePath) {
        List<Flight> out = new ArrayList<>();
        File f = new File(filePath);
        if (!f.exists()) return out;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                List<String> cols = parseCSVLine(line);
                if (cols.size() < 9) {
                    System.err.println("Error on line " + lineNumber + ": Incorrect number of columns. Expected 9, found " + cols.size());
                    continue;
                }
                try {
                    int id = Integer.parseInt(cols.get(0));
                    String airline = cols.get(1);
                    String from = cols.get(2);
                    String to = cols.get(3);
                    String dep = cols.get(4);
                    String arr = cols.get(5);
                    int total = Integer.parseInt(cols.get(6));
                    int avail = Integer.parseInt(cols.get(7));
                    double price = Double.parseDouble(cols.get(8));
                    out.add(new Flight(id, airline, from, to, dep, arr, total, avail, price));
                } catch (NumberFormatException e) {
                    System.err.println("Error on line " + lineNumber + ": Invalid number format. Details: " + e.getMessage());
                    System.err.println("Problematic line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading flights: " + e.getMessage());
        }
        return out;
    }

    public static List<Booking> readBookings(String filePath) {
        List<Booking> out = new ArrayList<>();
        File f = new File(filePath);
        if (!f.exists()) return out;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                List<String> cols = parseCSVLine(line);
                if (cols.size() < 6) continue;
                try {
                    int bookingId = Integer.parseInt(cols.get(0));
                    int flightId = Integer.parseInt(cols.get(1));
                    String passengerName = cols.get(2);
                    int seatsBooked = Integer.parseInt(cols.get(3));
                    double amountPaid = Double.parseDouble(cols.get(4));
                    String bookingDate = cols.get(5);
                    out.add(new Booking(bookingId, flightId, passengerName, seatsBooked, amountPaid, bookingDate));
                } catch (NumberFormatException e) {
                    System.err.println("Skipping malformed booking entry: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading bookings: " + e.getMessage());
        }
        return out;
    }

    public static void writeFlights(String filePath, List<Flight> flights) {
        File dir = new File(filePath).getParentFile();
        if (dir != null) dir.mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            for (Flight f : flights) pw.println(f.toCSV());
        } catch (IOException e) {
            System.err.println("Error writing flights: " + e.getMessage());
        }
    }

    public static void appendBooking(String filePath, String line) {
        try {
            File dir = new File(filePath).getParentFile();
            if (dir != null) dir.mkdirs();
            try (PrintWriter pw = new PrintWriter(new FileWriter(filePath, true))) {
                pw.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error writing bookings: " + e.getMessage());
        }
    }

    public static List<String> readAllLines(String filePath) {
        List<String> out = new ArrayList<>();
        File f = new File(filePath);
        if (!f.exists()) return out;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                out.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return out;
    }

    public static List<String> parseCSVLine(String line) {
        List<String> cols = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        cur.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    cur.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    cols.add(cur.toString());
                    cur.setLength(0);
                } else {
                    cur.append(c);
                }
            }
        }
        cols.add(cur.toString());
        return cols;
    }
}