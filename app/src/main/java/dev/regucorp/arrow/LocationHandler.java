package dev.regucorp.arrow;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import android.view.OrientationEventListener;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;

public class LocationHandler extends LocationCallback {

    public static final String TAG = "LocationHandler";

    private FusedLocationProviderClient locationManager;
    private Geocoder geocoder;

    private LocationHandlerListener locationListener;
    private long locationListenerInterval;



    public LocationHandler(Context context) {
        locationManager = LocationServices.getFusedLocationProviderClient(context);
        geocoder = new Geocoder(context);
    }

    public interface LocationHandlerListener {
        void onNewLocation(Location location);
    }

    private boolean checkPermissions(Context context) {
        return (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    public void configureHandler(double interval, LocationHandlerListener listener) {
        this.locationListener = listener;
        this.locationListenerInterval = (long) interval * 1000;
    }



    /* == Location retriever =============================================================================================== */

    @SuppressLint("MissingPermission")
    public void start(Context context) {
        if(!checkPermissions(context)) return;

        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(locationListenerInterval)
                .setFastestInterval(locationListenerInterval)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationManager.requestLocationUpdates(locationRequest, this, Looper.getMainLooper());
    }

    public void stop() {
        locationManager.removeLocationUpdates(this);
    }

    @Override
    public void onLocationResult(LocationResult locationResult) {
        super.onLocationResult(locationResult);
        Location l = locationResult.getLastLocation();
        if(l != null && locationListener != null) locationListener.onNewLocation(l);
    }

    public float getDeclination(Location location) {
        GeomagneticField geoField = new GeomagneticField(
                Double.valueOf(location.getLatitude()).floatValue(),
                Double.valueOf(location.getLongitude()).floatValue(),
                Double.valueOf(location.getAltitude()).floatValue(),
                System.currentTimeMillis()
        );

        return geoField.getDeclination();
    }



    /* == Address parsing ================================================================================================== */

    public Location getLocationOf(String address) {
        try {
            List<Address> locations = geocoder.getFromLocationName(address, 1);
            Location location = new Location("dummy");
            location.setLatitude(locations.get(0).getLatitude());
            location.setLongitude(locations.get(0).getLongitude());
            return location;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
