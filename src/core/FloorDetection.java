package core;

import model.BuildingInfo;

public class FloorDetection {
    BuildingInfo buildingInfo;
    double airPressure;

    public FloorDetection(BuildingInfo buildingInfo, double airPressure) {
        this.buildingInfo = buildingInfo;
        this.airPressure = airPressure;
    }

    public int detectFloor() {
        double refPoint = (2.746 * buildingInfo.getRefPressure()) / .1;
        double alt = (2.746 * airPressure) / .1;
        double elevation = refPoint - alt;
        int floor = (int) Math.round(elevation / buildingInfo.getFloorHeight());

        return Math.min(floor, buildingInfo.getStories());
    }
}
