package edu.temple.convoy;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

public class LocationService extends Service {

    private final LocationCallback locationcallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null && locationResult.getLocations() != null) {
                LatLng latlng = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());


            }


        }
    };



        private void startLocationService() {
            String channelID = "location_notification_channel";
            Intent resultIntent = new Intent();
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelID);

            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle("Location Service");
            builder.setDefaults(NotificationCompat.DEFAULT_ALL);
            builder.setContentText(("Running"));
            builder.setContentIntent(pendingIntent);
            builder.setAutoCancel(false);
            builder.setPriority(NotificationCompat.PRIORITY_MAX);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (notificationManager != null && notificationManager.getNotificationChannel(channelID) == null) {
                    NotificationChannel notificationChannel = new NotificationChannel(channelID, "Location Service", NotificationManager.IMPORTANCE_HIGH);
                    notificationChannel.setDescription("Used by location service");
                    notificationManager.createNotificationChannel(notificationChannel);
                }
            }
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(10);
            locationRequest.setFastestInterval(10);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            if (ActivityCompat.checkSelfPermission(LocationService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LocationService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                LocationServices.getFusedLocationProviderClient(LocationService.this).requestLocationUpdates(locationRequest, locationcallback, Looper.getMainLooper());
                startForeground(constant.Location_Service_ID, builder.build());

            }

        }


    private void stopLocationService(){
            LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationcallback);
            stopForeground(true);
            stopSelf();

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet Implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            String action = intent.getAction();
            if(action!=null){
                if(action.equals(constant.Action_Start_Location_Service)){
                    startLocationService();
                }
                else if (action.equals(constant.Action_Stop_Location_Service)){
                    stopLocationService();
                }
            }
        }
        return super.onStartCommand(intent,flags,startId);
    }
}



