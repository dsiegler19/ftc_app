package com.borsch.sim;

import com.vuforia.Matrix34F;
import com.vuforia.SimulatedTrackableResult;
import com.vuforia.TrackableResult;
import com.vuforia.VuMarkTarget;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.*;
import org.firstinspires.ftc.robotcore.internal.vuforia.VuforiaTrackableContainer;
import org.firstinspires.ftc.robotcore.internal.vuforia.VuforiaTrackableNotify;
import org.firstinspires.ftc.robotcore.internal.vuforia.VuforiaTrackablesImpl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimulatedVuforiaTrackable implements VuforiaTrackable, VuforiaTrackableNotify, VuforiaTrackableContainer {

    protected VuforiaTrackable parent;
    protected VuforiaTrackables trackables;
    protected String name;
    protected Listener listener;
    protected Object userData;
    protected final Object locationLock;
    protected OpenGLMatrix location;
    protected Class<? extends Listener> listenerClass;
    protected final Map<VuMarkInstanceId, VuforiaTrackable> vuMarkMap;

    public Matrix34F pose;
    public int status;

    public SimulatedVuforiaTrackable() {
        this(new Matrix34F(), TrackableResult.STATUS.UNKNOWN);
    }

    public SimulatedVuforiaTrackable(Matrix34F pose, int status) {
        this.locationLock = new Object();
        this.vuMarkMap = new HashMap();
        this.parent = null;
        this.trackables = null;
        this.userData = null;
        this.location = null;
        this.name = null;
        this.listenerClass = SimulatedVuforiaTrackableDefaultListener.class;
        this.pose = pose;
        this.status = status;

        try {
            Constructor ctor = listenerClass.getConstructor(VuforiaTrackable.class);

            try {
                this.listener = (Listener)ctor.newInstance(this);
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException var7) {
                throw new RuntimeException("unable to instantiate " + listenerClass.getSimpleName(), var7);
            }
        } catch (NoSuchMethodException var8) {
            throw new RuntimeException("class " + listenerClass.getSimpleName() + " missing VuforiaTrackable ctor");
        }
    }

    public VuforiaTrackable getChild(VuMarkTarget vuMarkTarget) {
        Map var2 = this.vuMarkMap;
        synchronized(this.vuMarkMap) {
            VuMarkInstanceId instanceId = new VuMarkInstanceId(vuMarkTarget.getInstanceId());
            VuforiaTrackable result = (VuforiaTrackable)this.vuMarkMap.get(instanceId);

            return (VuforiaTrackable)result;
        }
    }

    public List<VuforiaTrackable> children() {
        Map var1 = this.vuMarkMap;
        synchronized(this.vuMarkMap) {
            return new ArrayList(this.vuMarkMap.values());
        }
    }

    public synchronized void setListener(Listener listener) {
        this.listener = (Listener)(listener == null ? new VuforiaTrackableDefaultListener(this) : listener);
    }

    public synchronized Listener getListener() {
        return this.listener;
    }

    public synchronized void setUserData(Object object) {
        this.userData = object;
    }

    public synchronized Object getUserData() {
        return this.userData;
    }

    public VuforiaTrackables getTrackables() {
        return this.trackables;
    }

    public void setLocation(OpenGLMatrix location) {
        Object var2 = this.locationLock;
        synchronized(this.locationLock) {
            this.location = location;
        }
    }

    public OpenGLMatrix getLocation() {
        Object var1 = this.locationLock;
        synchronized(this.locationLock) {
            return this.location;
        }
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VuforiaTrackable getParent() {
        return this.parent;
    }

    @Override
    public void noteTracked(TrackableResult trackableResult) {

    }

    public synchronized void noteNotTracked() {
        this.getListener().onNotTracked();
    }

    public synchronized void noteTracked(SimulatedTrackableResult trackableResult) {
        this.getListener().onTracked(trackableResult, (VuforiaTrackable)null);
        if (this.parent instanceof VuforiaTrackableNotify) {
            this.parent.getListener().onTracked(trackableResult, this);
        }

    }
}
