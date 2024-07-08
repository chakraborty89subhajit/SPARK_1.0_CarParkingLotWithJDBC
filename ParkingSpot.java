package org.example;

public class ParkingSpot {

    int parking_spot_no;
    boolean available;
    private Car car;

    ParkingSpot(int parking_spot_no) {
        this.parking_spot_no = parking_spot_no;
        this.available = true;
        this.car = null;
    }

    public void occupy(Car car) {
        this.car = car;
        this.available = false;
    }

    public void vacant() {
        this.car = null;
        this.available = true;
    }

    public Car getCar() {
        return car;
    }

    public int getSpotNo() {
        return parking_spot_no;
    }

    public boolean isAvailable() {
        return available;
    }

}
