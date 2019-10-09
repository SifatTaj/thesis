package model;

public class FloorLayout {
    private String place;
    private int floor;
    private int height;
    private int width;

    public FloorLayout(String place, int floor, int height, int width, int[][] walls) {
        this.place = place;
        this.floor = floor;
        this.height = height;
        this.width = width;
        this.walls = walls;
    }

    private final int[][] walls;

    public String getPlace() {
        return place;
    }

    public int getFloor() {
        return floor;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int[][] getWalls() {
        return walls;
    }
}
