package com.example.jwt_demo.localdatabase;

import java.util.HashMap;
import java.util.Map;

public class Locations {

    private static final Map<String, String> locations = new HashMap<>();

    static {
        locations.put("Tallinn", "Tallinn");
        locations.put("Tartu", "Tartu");
        locations.put("Narva", "Narva");
        locations.put("Pärnu", "Pärnu");
        locations.put("Jõhvi", "Jõhvi");

    }

    public static Map<String, String> getLocations() {
        return locations;
    }
}
