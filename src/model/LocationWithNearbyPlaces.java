package model;

import java.util.ArrayList;

public class LocationWithNearbyPlaces {

    private String location;
    private ArrayList<LocDistance> places;

    public LocationWithNearbyPlaces(String location, ArrayList<LocDistance> places) {
        this.location = location;
        this.places = places;
    }

    public String getLocation() {
        return location;
    }

    public ArrayList<LocDistance> getPlaces() {
        return places;
    }
}
