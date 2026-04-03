package com.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

import javafx.collections.ObservableList;

public class CsvManager {
    private final String filePath;

    public CsvManager(String filePath) {
        this.filePath = filePath;
    }

    public void save(ObservableList<Room> rooms) {
        try (PrintWriter pw = new PrintWriter(new File(filePath))) {
            for (Room r : rooms) pw.println(r.toCSV());
        } catch (Exception e) {
            System.err.println("Save Error: " + e.getMessage());
        }
    }

    public void load(ObservableList<Room> rooms) {
        File f = new File(filePath);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length < 8) continue;
                rooms.add(new Room(
                    p[0], p[1],
                    Double.parseDouble(p[2]),
                    Boolean.parseBoolean(p[3]),
                    p[4].equals("N/A") ? "" : p[4],
                    p[5].equals("N/A") ? "" : p[5],
                    p[6].equals("N/A") ? "" : p[6],
                    Integer.parseInt(p[7])
                ));
            }
        } catch (Exception e) {
            System.err.println("Load Error: " + e.getMessage());
        }
    }
}