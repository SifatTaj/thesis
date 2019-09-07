package model;

import java.util.ArrayList;

public class ReferencePoint {

    private String id;
    private String name;
    private String description;
    private double x;
    private double y;
//    Important: must set it as: x y (space in between)
//    private String locId;
//    Important: These readings list count must be equal to the number of APS in area.
//    If some AP is not accesible at this RP then put the least RSS value i.e. NaN in Algorithms.java
    private ArrayList<AccessPoint> readings;

    public ReferencePoint(String id, String name, String description, double x, double y, ArrayList<AccessPoint> readings) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.x = x;
        this.y = y;
        this.readings = readings;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public ArrayList<AccessPoint> getReadings() {
        return readings;
    }
}
