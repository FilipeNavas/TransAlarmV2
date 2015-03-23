package com.slodin.transalarm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Map;


public class MainActivity extends ActionBarActivity implements
        ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<Status> {

    protected static final String TAG = "monitoring-geofences";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * The list of geofences used in this sample.
     */
    protected ArrayList<Geofence> mGeofenceList;

    /**
     * Used to keep track of whether geofences were added.
     */
    private boolean mGeofencesAdded;

    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;

    /**
     * Used to persist application state about whether geofences were added.
     */
    private SharedPreferences mSharedPreferences;




    @Override
    @JavascriptInterface
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        int screenWidth = this.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = this.getResources().getDisplayMetrics().heightPixels;

        //Map displayed in webview
        final WebView webView = (WebView)findViewById(R.id.webView);
        WebSettings webSetting = webView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);

        //Toast.makeText(getApplicationContext(), "Debug: "+"Height: " + (screenHeight) +"   " + "Width: " + screenWidth, Toast.LENGTH_LONG ).show();

        if(screenWidth == 720 && screenHeight == 1280) {
            webView.setInitialScale(97);
        }
        else if(screenWidth == 1080 && screenHeight == 1776)
        {
            webView.setInitialScale(139);
        }
        else
        {
            webView.setInitialScale(97);
        }
        webView.getSettings().setUseWideViewPort(true);

        webView.loadUrl("file:///android_asset/Map/skytrainMap.html");

        //This creates an interface to use on the JavaScript code.
        //In this case Android is the interface that you will use in the JavaScript
        webView.addJavascriptInterface(new WebAppInterface(this), "Android");



        // Get the UI widgets.
        /*mAddGeofencesButton = (Button) findViewById(R.id.add_geofences_button);
        mRemoveGeofencesButton = (Button) findViewById(R.id.remove_geofences_button);
*/
        // Empty list for storing geofences.
        mGeofenceList = new ArrayList<Geofence>();

        // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
        mGeofencePendingIntent = null;

        // Retrieve an instance of the SharedPreferences object.
        mSharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,
                MODE_PRIVATE);

        // Get the value of mGeofencesAdded from SharedPreferences. Set to false as a default.
        mGeofencesAdded = mSharedPreferences.getBoolean(Constants.GEOFENCES_ADDED_KEY, false);

        //It is needed to call this method here otherwise the app crashes
        // Get the geofences used. Geofence data is hard coded in this sample.
        populateGeofenceList();

        // Kick off the request to build GoogleApiClient.
        buildGoogleApiClient();

    }


    //Activity onDestroy should remove all geofences and disconnect from Google Play Services
    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeGeofencesHandler();
        mGoogleApiClient.disconnect();

    }


    protected boolean checkGPSisEnabled(){

      //get the last known location - the current location
       Location mLastLocation;
       mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

      if (mLastLocation != null){
            return true;
            //Toast.makeText(this, "GPS is Enabled in your device", Toast.LENGTH_SHORT).show();
        }else{
          return false;
        }
    }


    public static void displayPromptForEnablingGPS(

            final Activity activity){
                final AlertDialog.Builder builder =
                        new AlertDialog.Builder(activity);
                final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
                final String message = "The GPS is disabled."
                        + " Please enable it to continue.";

                builder.setMessage(message)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int id) {
                                        activity.startActivity(new Intent(action));
                                        d.dismiss();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int id) {
                                        d.cancel();
                                    }
                                });
                builder.create().show();
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    /**
     * Adds geofences, which sets alerts to be notified when the device enters or exits one of the
     * specified geofences. Handles the success or failure results returned by addGeofences().
     */
    public void addGeofencesHandler() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    /**
     * Removes geofences, which stops further notifications when the device enters or exits
     * previously registered geofences.
     */
    public void removeGeofencesHandler() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            // Remove geofences.
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    // This is the same pending intent that was used in addGeofences().
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

    /**
     * Runs when the result of calling addGeofences() and removeGeofences() becomes available.
     * Either method can complete successfully or with an error.
     *
     * Since this activity implements the {@link ResultCallback} interface, we are required to
     * define this method.
     *
     * @param status The Status returned through a PendingIntent when addGeofences() or
     *               removeGeofences() get called.
     */
    public void onResult(Status status) {
        if (status.isSuccess()) {
            // Update state and save in shared preferences.
            mGeofencesAdded = !mGeofencesAdded;
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(Constants.GEOFENCES_ADDED_KEY, mGeofencesAdded);
            editor.commit();

            // Update the UI. Adding geofences enables the Remove Geofences button, and removing
            // geofences enables the Add Geofences button.
            //setButtonsEnabledState();

            /*Toast.makeText(
                    this,
                    getString(mGeofencesAdded ? R.string.geofences_added :
                            R.string.geofences_removed),
                    Toast.LENGTH_SHORT
            ).show();*/
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    status.getStatusCode());
            Log.e(TAG, errorMessage);
        }
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * This sample hard codes geofence data. A real app might dynamically create geofences based on
     * the user's location.
     * Method called on the MainActivity OnCreate method, so all the Geofences are added
     */
    public void populateGeofenceList() {

        for (Map.Entry<String, LatLng> entry : Constants.SKYTRAIN_STATION.entrySet()) {

            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(entry.getKey())

                            // Set the circular region of this geofence.
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )

                            // Set the expiration duration of the geofence. This geofence gets automatically
                            // removed after this period of time.
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                            // Set the transition types of interest. Alerts are only generated for these
                            // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)

                            //Sets the best-effort notification responsiveness of the geofence.
                    .setNotificationResponsiveness(0)

                            // Create the geofence.
                    .build());
        }
    }


    /**
     * This function add the DESTINATION geofence and removes the hard-coded one
     */
    public void populateGeofenceDestination(String oldId, LatLng coordinates) {

            //Remove the hard-coded from the Geofence List
            for (int i = mGeofenceList.size()-1; i> -1; i--) {
                if (mGeofenceList.get(i).getRequestId().equals(oldId)){
                    mGeofenceList.remove(mGeofenceList.get(i));
                }
            }

            //Add the DESTINATION Geofence
            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId("DESTINATION")

                            // Set the circular region of this geofence.
                    .setCircularRegion(
                            coordinates.latitude,
                            coordinates.longitude,
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )

                            // Set the expiration duration of the geofence. This geofence gets automatically
                            // removed after this period of time.
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                            // Set the transition types of interest. Alerts are only generated for these
                            // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)

                            //.setLoiteringDelay(3000)
                            // Create the geofence.
                    .build());

    }











 /*Parses a String into a LatLng object
 @param coordinates String
 @return LatLng latLng   */
  protected  LatLng parseLatitudeLongitude(String coordinates){


      try {
          //It parses the coordinates to double
          String[] latlong = coordinates.split(",");
          double latitude = Double.parseDouble(latlong[0]);
          double longitude = Double.parseDouble(latlong[1]);

          //create a LatLng variable that keeps the coordinates
          //The Geocoder needs this type of parameter
          LatLng latLng = new LatLng(latitude, longitude);

          return latLng;
      }catch(Exception e){
          Toast.makeText(this, "Sorry, we got an error when parsing the coordinates :/", Toast.LENGTH_SHORT).show();
          return null;
      }


  }

    public float getDistanceInMiles() {

        Location mLastLocation;
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        double lat1 = ((double)mLastLocation.getLatitude()) / 1e6;
        double lng1 = ((double)mLastLocation.getLongitude()) / 1e6;
        double lat2 = (49.285732) / 1e6;
        double lng2 = (-123.111770) / 1e6;
        float [] dist = new float[1];
        Location.distanceBetween(lat1, lng1, lat2, lng2, dist);

        return dist[0] * 1000;
    }

    protected Float getSpeed(){

        try {

            Float speed;

            //get the last known location - the current location
            Location mLastLocation;
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            //returns in Km/h
            speed = (mLastLocation.getSpeed() * (3.6f));

            return (speed);
            //Toast.makeText(this,"Speed: " + speed, Toast.LENGTH_LONG).show();
        }catch(Exception e){
            Log.e(TAG, e.toString());
            return null;
        }

    }


    //Calculates the distance from the current location to a given coordinates
    //@return String distance in Km
    protected String distanceTo(String coordinates){


        try {

            Float result;
            String resultFinal;


            //It parses the coordinates to double
            String[] latLong = coordinates.split(",");
            double latitude = Double.parseDouble(latLong[0]);
            double longitude = Double.parseDouble(latLong[1]);

            //create a new Location object and set the lat and longitude
            Location destinationLocation = new Location("");
            destinationLocation.setLatitude(latitude);
            destinationLocation.setLongitude(longitude);
            //zeroLocation.setLongitude(-123.111770  / 1e6);

            //get the last known location - the current location
            Location mLastLocation;
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);



            //Toast.makeText(this,"Current Location (Lat/Long): " + mLastLocation.getLatitude() + " - " +  mLastLocation.getLongitude(), Toast.LENGTH_LONG).show();

            //calculates the distance from current location to the destination
            result = mLastLocation.distanceTo(destinationLocation);

            result = result / 1000;

            resultFinal = String.format("%.3f", result);

            return  resultFinal;


        }catch(Exception e){
            Toast.makeText(this, "Sorry, we got an error when getting the distance :/", Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.toString());
            return null;
        }

    }

    protected  String getEstimatedArrivalTime(String distance, Float speed){

        //get the time
        // Formula: Time = Distance ÷ Speed
        // Times 60 to get in Minutes
        try {

            //In case the speed is 0 (user is stopped or there was an error getting the speed,
            // it is assigned an average speed to calculate the estimated time.
            //Average time = 45 km/h - http://en.wikipedia.org/wiki/Skytrain_rolling_stock#Expo_Line_and_Millennium_Line
            //Although, I'll set up an average of 30 km/h, based on experience
            if (speed == null || speed == 0){
                speed = 30.0f;
            }

            Float estimatedTime = (Float.valueOf(distance) / speed) * 60;

            return String.format("%.0f", estimatedTime);
        }
        catch(Exception e){
                Toast.makeText(this, "Sorry, we got an error when getting the estimated time :/", Toast.LENGTH_SHORT).show();
                Log.e(TAG, e.toString());
                return null;
        }

    }





    //***********************************************************************************
    //***********************************************************************************
    //***********************************************************************************
    //************************** WEB APP INTERFACE***************************************
    //***********************************************************************************
    //***********************************************************************************



    /*  SubClass that has the Android interface with the JavaScript and the methods
        Filipe 2015-03-04 http://developer.android.com/guide/webapps/webview.html
        Class that is a bridge between the JavaScript and Android
        Actually, this class will have the methods that you will call from the JavaScript file
    */
    public class WebAppInterface
    {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /*
        Function calls the Detail Activity when destination has been selected
        Set 2 extra parameters - station Id and Station Name from the HTML document
        @param String stationId - station id only
        @param String stationLabel - the station name well formatted
        @param String coordinates - Latitude and Longitude of the selected station*/
        @JavascriptInterface
        public void setDestination( final String stationId, final String stationLabel, final String coordinates) {

            try{


                    //****************CHECK IF GPS IS ENABLED***************
                    // Check if the GPS is enabled,if not open the activity for the user to set it on
                    //If it is not enabled then call the function to open a dialog and activate it,
                    //if it's enabled go on

                    if(!checkGPSisEnabled()){

                        displayPromptForEnablingGPS(MainActivity.this);

                    }else{


                        //gets the coordinates in LatLng object
                        LatLng latLng =  parseLatitudeLongitude(coordinates);


                        //Populate the DESTINATION and remove the hard-coded selected station from the Geofences List
                        populateGeofenceDestination(stationId,latLng);

                        //the app creates the Geofence Handler, which will watch for events to ttrigger
                        addGeofencesHandler();


                        String distanceTo = distanceTo(coordinates);
                        String estimatedTime = getEstimatedArrivalTime(distanceTo,getSpeed());

                        //Toast.makeText(mContext,"Setting up your destination..." , Toast.LENGTH_SHORT).show();

                        //Toast.makeText(mContext,"Distance to " + stationLabel + ": " + distanceTo + " Km" , Toast.LENGTH_SHORT).show();
                        Toast.makeText(mContext,"Estimated Travel Time: " + estimatedTime + " Minutes" , Toast.LENGTH_SHORT).show();


                        Intent afterSelect = new Intent(mContext, calPage.class)
                                        .putExtra(Intent.EXTRA_TEXT, stationId)
                                        .putExtra(Intent.EXTRA_SHORTCUT_NAME, stationLabel)
                                        .putExtra(Intent.EXTRA_TITLE, estimatedTime);
                        startActivity(afterSelect);


                    }

            }catch(Exception e ){
                Log.e(TAG, e.toString());
            }

        }

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

