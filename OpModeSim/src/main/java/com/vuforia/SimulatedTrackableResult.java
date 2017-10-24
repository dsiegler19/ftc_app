//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.vuforia;

public class SimulatedTrackableResult extends TrackableResult {

    private Matrix34F pose;
    private int status;

    public SimulatedTrackableResult(Matrix34F pose, int status) {
        super(0L, true);
        this.pose = pose;
        this.status = status;
    }

    protected static long getCPtr(SimulatedTrackableResult obj) {
        return 0L;
    }

    protected void finalize() {
        this.delete();
    }

    protected synchronized void delete() {
        this.swigCMemOwn = false;
    }

    public boolean equals(Object obj) {
        return false;
    }

    public static Type getClassType() {
        return new Type(0L, true);
    }

    public Type getType() {
        return new Type(0L, true);
    }

    public boolean isOfType(Type type) {
        return true;
    }

    public double getTimeStamp() {
        return 0d;
    }

    public int getStatus() {
        return status;
    }

    public Trackable getTrackable() {
        return new Trackable(0L, true);
    }

    public Matrix34F getPose() {
        return pose;
    }

    public int getCoordinateSystem() {
        return 0;
    }
}
