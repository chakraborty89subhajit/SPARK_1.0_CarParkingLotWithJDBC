package org.example;

public class TestMain {
    public static void main(String args[]) {
        ParkingLot lot = new ParkingLot(5);
        Car car1 = new Car("wb33a1234");
        Car car2 = new Car("wb33a4567");
        Car car3 = new Car("wb33a9876");

        lot.parkCar(car1);
        lot.parkCar(car2);
        lot.parkCar(car3);

        lot.showAllCar(); // Call to show all parked cars
        lot.showAvailableSpot(); // Call to show available parking spots
    }
}
