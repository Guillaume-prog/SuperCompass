package dev.regucorp.arrow;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import dev.regucorp.arrow.smoothing.RollingAverage;

public class MainActivity extends AppCompatActivity implements LocationHandler.LocationHandlerListener, Compass.CompassListener {

    public static final String TAG = "MainActivity";

    private static final String address = "2 Av. du Professeur Jean Rouxel, 44470 Carquefou";
    private LocationHandler handler;
    private Compass compass;

    private ImageView spinner;
    private TextView distanceView;

    private Location destLocation;
    private RollingAverage distanceValues;
    private float bearing = 0, declination = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = findViewById(R.id.spinner);
        distanceView = findViewById(R.id.distance);

        // Compass setup
        compass = new Compass(this);
        compass.setCompassListener(this);

        // Location handler
        handler = new LocationHandler(this);
        handler.configureHandler(0.1, this);

        destLocation = handler.getLocationOf(address);
        distanceValues = new RollingAverage(10);
    }

    @Override
    public void onNewCompassValue(float heading) {
        Log.d(TAG, "onNewCompassValue - heading: "+ heading +", bearing: "+ bearing +", declination: "+ declination);

        float rotAngle = bearing + (heading + declination);
        spinner.setRotation(rotAngle);
    }

    @Override
    public void onNewLocation(Location location) {
        bearing = location.bearingTo(destLocation);
        declination = handler.getDeclination(location);

        distanceValues.add( location.distanceTo(destLocation) );
        distanceView.setText( (int) distanceValues.getAverage() + "m" );

        Log.d(TAG, "onNewLocation: New value");
    }

    @Override
    protected void onResume() {
        super.onResume();
        compass.start();
        handler.start(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        compass.stop();
        handler.stop();
    }
}