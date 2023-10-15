package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TrafficTrackerTest2 {
    private final static String VAULT_NAME = "/test1";

    TrafficTracker trafficTracker;

    @BeforeEach
    public void setup() {
        trafficTracker = new TrafficTracker();
        trafficTracker.importCarsFromVault(VAULT_NAME + "/cars.txt");
        trafficTracker.importDetectionsFromVault(VAULT_NAME + "/detections");
    }

    @Test
    public void calculateTotalFines() {
        double expectedTotalFines = 175.0;
        assertEquals(expectedTotalFines, trafficTracker.calculateTotalFines());
    }

    @Test
    public void topViolationsByCar() {
        List<String> expectedTopCar = List.of("227-HX-3");
        List<String> topCars = trafficTracker.topViolationsByCar(5).stream()
                .map(v -> v.getCar().getLicensePlate())
                .collect(Collectors.toList());
        assertEquals(expectedTopCar, topCars);
    }

    @Test
    public void topViolationsByCity() {
        List<String> expectedTopCities = List.of("Amsterdam", "Rotterdam");
        List<String> topCities = trafficTracker.topViolationsByCity(5).stream()
                .map(Violation::getCity)
                .collect(Collectors.toList());
        assertEquals(expectedTopCities, topCities);
    }
}
