package com.siu.android.arapp.common;

import com.siu.android.arapp.activity.AugmentedReality;

/**
 * A static class used to calculate azimuth, pitch, and roll given a rotation
 * matrix.
 *
 * This file was adapted from Mixare <http://www.mixare.org/>
 *
 * @author Daniele Gobbetti <info@mixare.org>
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class Calculator {

    private static final Vector looking = new Vector();
    private static final float[] lookingArray = new float[3];
    private static final Matrix tempMatrix = new Matrix();

    private static volatile float azimuth = 0;
    private static volatile float pitch = 0;
    private static volatile float roll = 0;

    private Calculator() { }

    /**
     * Get angle in degrees between two points.
     *
     * @param center_x Lesser point's X
     * @param center_y Lesser point's Y
     * @param post_x Greater point's X
     * @param post_y Greater point's Y
     * @return Angle in degrees
     */
    public static final float getAngle(float center_x, float center_y, float post_x, float post_y) {
        float delta_x = post_x - center_x;
        float delta_y = post_y - center_y;
        return (float)(Math.atan2(delta_y, delta_x) * 180 / Math.PI);
    }

    /**
     * Azimuth the phone's camera is pointing. From 0 to 360 with magnetic north
     * compensation.
     *
     * @return float representing the azimuth the phone's camera is pointing
     */
    public static synchronized float getAzimuth() {
        return Calculator.azimuth;
    }

    /**
     * Pitch of the phone's camera. From -90 to 90, where negative is pointing
     * down and zero is level.
     *
     * @return float representing the pitch of the phone's camera.
     */
    public static synchronized float getPitch() {
        return Calculator.pitch;
    }

    /**
     * Roll of the phone's camera. From -90 to 90, where negative is rolled left
     * and zero is level.
     *
     * @return float representing the roll of the phone's camera.
     */
    public static synchronized float getRoll() {
        return Calculator.roll;
    }

    /**
     * Calculate and populate the Azimuth, Pitch, and Roll.
     *
     * @param rotationMatrix Rotation matrix used in calculations.
     */
    public static synchronized void calcPitchBearing(Matrix rotationMatrix) {
        if (rotationMatrix == null) return;

        tempMatrix.set(rotationMatrix);
        tempMatrix.transpose();
        if (AugmentedReality.portrait) {
            looking.set(0, 1, 0);
        } else {
            looking.set(1, 0, 0);
        }
        looking.prod(tempMatrix);
        looking.get(lookingArray);
        Calculator.azimuth = ((getAngle(0, 0, lookingArray[0], lookingArray[2]) + 360) % 360);
        Calculator.roll = -(90 - Math.abs(getAngle(0, 0, lookingArray[1], lookingArray[2])));
        looking.set(0, 0, 1);
        looking.prod(tempMatrix);
        looking.get(lookingArray);
        Calculator.pitch = -(90 - Math.abs(getAngle(0, 0, lookingArray[1], lookingArray[2])));
    }
}

