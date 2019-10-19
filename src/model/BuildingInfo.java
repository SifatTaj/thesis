package model;

public class BuildingInfo {
    private final int stories;
    private final double refPressure;
    private final double floorHeight;

    public BuildingInfo(int stories, double refPressure, double floorHeight) {
        this.stories = stories;
        this.refPressure = refPressure;
        this.floorHeight = floorHeight;
    }

    public int getStories() {
        return stories;
    }

    public double getRefPressure() {
        return refPressure;
    }

    public double getFloorHeight() {
        return floorHeight;
    }
}
