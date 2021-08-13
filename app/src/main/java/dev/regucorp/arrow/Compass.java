package dev.regucorp.arrow;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import dev.regucorp.arrow.smoothing.AngleSmoothingFilter;

public class Compass implements SensorEventListener {

    private static final String TAG = "Compass";

    private SensorManager sensorManager;
    private CompassListener compassListener;

    private float[] gravity;
    private float[] accels = new float[3], mags = new float[3], values = new float[3];

    private AngleSmoothingFilter angleList;

    public interface CompassListener {
        void onNewCompassValue(float angle);
    }



    public Compass(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        angleList = new AngleSmoothingFilter(15);
    }

    public void setCompassListener(CompassListener listener) {
        this.compassListener = listener;
    }



    public void start() {
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                mags = event.values.clone();
                break;
            case Sensor.TYPE_ACCELEROMETER:
                accels = event.values.clone();
                break;
        }

        if (mags != null && accels != null) {
            gravity = new float[9];

            SensorManager.getRotationMatrix(gravity, null, accels, mags);
            SensorManager.getOrientation(gravity, values);

            float angle = (float) (-Math.toDegrees(values[0]) + 360) % 360;
            angleList.add(angle);

            if(compassListener != null) compassListener.onNewCompassValue((float) angleList.getAverage());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
