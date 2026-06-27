package com.mediaplayerapp.model;

public class EQPreset {

    private long id;
    private String name;
    private double[] gains;
    private boolean builtIn;

    public EQPreset() {
        this.gains = new double[10];
    }

    public EQPreset(String name, double[] gains, boolean builtIn) {
        this.name = name;
        this.gains = gains;
        this.builtIn = builtIn;
    }

    public static EQPreset flat() {
        return new EQPreset("Flat", new double[]{0,0,0,0,0,0,0,0,0,0}, true);
    }

    public static EQPreset bassBoost() {
        return new EQPreset("Bass Boost", new double[]{6,5,4,3,1,0,0,0,0,0}, true);
    }

    public static EQPreset rock() {
        return new EQPreset("Rock", new double[]{4,3,2,1,-1,-1,1,2,3,4}, true);
    }

    public static EQPreset pop() {
        return new EQPreset("Pop", new double[]{-1,2,4,4,2,0,-1,-1,-1,-1}, true);
    }

    public static EQPreset jazz() {
        return new EQPreset("Jazz", new double[]{3,2,1,2,0,0,1,2,3,3}, true);
    }

    public static EQPreset classical() {
        return new EQPreset("Classical", new double[]{4,3,2,1,0,0,-1,-2,2,3}, true);
    }

    public static EQPreset vocal() {
        return new EQPreset("Vocal", new double[]{-2,-1,0,2,4,4,3,1,0,-1}, true);
    }

    public static EQPreset electronic() {
        return new EQPreset("Electronic", new double[]{5,4,1,0,-2,-2,0,2,4,5}, true);
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double[] getGains() { return gains; }
    public void setGains(double[] gains) { this.gains = gains; }

    public boolean isBuiltIn() { return builtIn; }
    public void setBuiltIn(boolean builtIn) { this.builtIn = builtIn; }

    public double getGain(int band) {
        if (band >= 0 && band < gains.length) return gains[band];
        return 0;
    }

    public void setGain(int band, double value) {
        if (band >= 0 && band < gains.length) gains[band] = value;
    }

    @Override
    public String toString() { return name; }
}
