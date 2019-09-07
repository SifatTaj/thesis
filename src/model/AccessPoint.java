package model;

public class AccessPoint {

    private String id;
    private String description;
    private String ssid;
    private String mac_address;
    private double x;
    private double y;
    private double meanRss;//for RP (-50 to -100)
//    High quality: 90% ~= -55db
//    Medium quality: 50% ~= -75db
//    Low quality: 30% ~= -85db
//    Unusable quality: 8% ~= -96db

    public AccessPoint(String id, String description, String ssid, String mac_address, double x, double y, double meanRss) {
        this.id = id;
        this.description = description;
        this.ssid = ssid;
        this.mac_address = mac_address;
        this.x = x;
        this.y = y;
        this.meanRss = meanRss;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getSsid() {
        return ssid;
    }

    public String getMac_address() {
        return mac_address;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getMeanRss() {
        return meanRss;
    }
}
