package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ParkingLot {
    List<ParkingSpot> spots;

    public ParkingLot(int capacity) {
        this.spots = new ArrayList<>();
        for (int i = 0; i < capacity; i++) {
            spots.add(new ParkingSpot(i));
        }

        // Initialize parking spots in the database
        try (Connection conn = DatabaseConnection.getConnection()) {
            for (ParkingSpot spot : spots) {
                String insertSpotSQL = "INSERT INTO ParkingSpot (spot_no, available) VALUES (?, ?)";
                PreparedStatement spotStmt = conn.prepareStatement(insertSpotSQL);
                spotStmt.setInt(1, spot.getSpotNo());
                spotStmt.setBoolean(2, spot.isAvailable());
                spotStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean parkCar(Car car) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            for (ParkingSpot spot : spots) {
                if (spot.isAvailable()) {
                    String insertCarSQL = "INSERT INTO Car (licence_plate_no) VALUES (?)";
                    PreparedStatement carStmt = conn.prepareStatement(insertCarSQL, PreparedStatement.RETURN_GENERATED_KEYS);
                    carStmt.setString(1, car.getLicence_plate_no());
                    carStmt.executeUpdate();

                    ResultSet generatedKeys = carStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int carId = generatedKeys.getInt(1);
                        String updateSpotSQL = "UPDATE ParkingSpot SET available = false, car_id = ? WHERE spot_no = ?";
                        PreparedStatement spotStmt = conn.prepareStatement(updateSpotSQL);
                        spotStmt.setInt(1, carId);
                        spotStmt.setInt(2, spot.getSpotNo());
                        spotStmt.executeUpdate();

                        spot.occupy(car);
                        System.out.println("Car parked: " + car.getLicence_plate_no() + " in spot " + spot.getSpotNo());
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeCar(String licence_plate_no) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String selectCarSQL = "SELECT id FROM Car WHERE licence_plate_no = ?";
            PreparedStatement selectCarStmt = conn.prepareStatement(selectCarSQL);
            selectCarStmt.setString(1, licence_plate_no);
            ResultSet rs = selectCarStmt.executeQuery();
            if (rs.next()) {
                int carId = rs.getInt("id");
                String updateSpotSQL = "UPDATE ParkingSpot SET available = true, car_id = NULL WHERE car_id = ?";
                PreparedStatement updateSpotStmt = conn.prepareStatement(updateSpotSQL);
                updateSpotStmt.setInt(1, carId);
                updateSpotStmt.executeUpdate();

                String deleteCarSQL = "DELETE FROM Car WHERE id = ?";
                PreparedStatement deleteCarStmt = conn.prepareStatement(deleteCarSQL);
                deleteCarStmt.setInt(1, carId);
                deleteCarStmt.executeUpdate();

                for (ParkingSpot spot : spots) {
                    if (!spot.isAvailable() && spot.getCar().getLicence_plate_no().equalsIgnoreCase(licence_plate_no)) {
                        spot.vacant();
                        System.out.println("Car with " + licence_plate_no + " removed from spot " + spot.getSpotNo());
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Car with licence plate " + licence_plate_no + " not found");
        return false;
    }

    public void showAllCar() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String selectAllCarsSQL = "SELECT Car.licence_plate_no, ParkingSpot.spot_no FROM Car JOIN ParkingSpot ON Car.id = ParkingSpot.car_id";
            PreparedStatement stmt = conn.prepareStatement(selectAllCarsSQL);
            ResultSet rs = stmt.executeQuery();
            System.out.println("All parked cars:");
            while (rs.next()) {
                System.out.println("Spot " + rs.getInt("spot_no") + ": " + rs.getString("licence_plate_no"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void showAvailableSpot() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String selectAvailableSpotsSQL = "SELECT spot_no FROM ParkingSpot WHERE available = true";
            PreparedStatement stmt = conn.prepareStatement(selectAvailableSpotsSQL);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Available parking spots:");
            while (rs.next()) {
                System.out.println("Spot " + rs.getInt("spot_no") + " is available.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
