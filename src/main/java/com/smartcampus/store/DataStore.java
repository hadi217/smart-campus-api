package com.smartcampus.store;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DataStore {

    private static final DataStore INSTANCE = new DataStore();
    public static DataStore getInstance() { return INSTANCE; }

    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private final Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();

    private DataStore() {
        // Seed rooms
        Room r1 = new Room("LIB-301", "Library Quiet Study", 50);
        Room r2 = new Room("LAB-101", "Computer Science Lab", 30);
        Room r3 = new Room("HALL-A",  "Main Hall", 200);
        rooms.put(r1.getId(), r1);
        rooms.put(r2.getId(), r2);
        rooms.put(r3.getId(), r3);

        // Seed sensors
        Sensor s1 = new Sensor("TEMP-001", "Temperature", "ACTIVE",      21.5, "LIB-301");
        Sensor s2 = new Sensor("CO2-001",  "CO2",         "ACTIVE",     412.0, "LIB-301");
        Sensor s3 = new Sensor("OCC-001",  "Occupancy",   "MAINTENANCE",  0.0, "LAB-101");
        Sensor s4 = new Sensor("TEMP-002", "Temperature", "OFFLINE",     19.0, "HALL-A");
        sensors.put(s1.getId(), s1);
        sensors.put(s2.getId(), s2);
        sensors.put(s3.getId(), s3);
        sensors.put(s4.getId(), s4);

        // Link sensors to rooms
        r1.getSensorIds().add("TEMP-001");
        r1.getSensorIds().add("CO2-001");
        r2.getSensorIds().add("OCC-001");
        r3.getSensorIds().add("TEMP-002");

        // Seed some readings for TEMP-001
        List<SensorReading> r1Readings = new ArrayList<>();
        r1Readings.add(new SensorReading("READ-0001", System.currentTimeMillis() - 60000, 21.0));
        r1Readings.add(new SensorReading("READ-0002", System.currentTimeMillis(), 21.5));
        readings.put("TEMP-001", r1Readings);
    }

    // Room methods
    public Collection<Room> getAllRooms() { return rooms.values(); }
    public Room getRoom(String id) { return rooms.get(id); }
    public void addRoom(Room room) { rooms.put(room.getId(), room); }
    public boolean roomExists(String id) { return rooms.containsKey(id); }
    public boolean deleteRoom(String id) { return rooms.remove(id) != null; }

    // Sensor methods
    public Collection<Sensor> getAllSensors() { return sensors.values(); }
    public Sensor getSensor(String id) { return sensors.get(id); }
    public void addSensor(Sensor sensor) {
        sensors.put(sensor.getId(), sensor);
        readings.putIfAbsent(sensor.getId(), new ArrayList<>());
    }
    public boolean sensorExists(String id) { return sensors.containsKey(id); }

    // Reading methods
    public List<SensorReading> getReadings(String sensorId) {
        return readings.computeIfAbsent(sensorId, k -> new ArrayList<>());
    }
    public void addReading(String sensorId, SensorReading reading) {
        getReadings(sensorId).add(reading);
    }
}