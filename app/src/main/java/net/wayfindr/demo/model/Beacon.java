package net.wayfindr.demo.model;

public class Beacon {
    public final String serialNumber;
    public final int major;
    public final int minor;

    public Beacon(String serialNumber, int major, int minor) {
        this.serialNumber = serialNumber;
        this.major = major;
        this.minor = minor;
    }

    @Override
    public String toString() {
        return "Beacon{" +
                "serialNumber='" + serialNumber + '\'' +
                ", major=" + major +
                ", minor=" + minor +
                '}';
    }
}
