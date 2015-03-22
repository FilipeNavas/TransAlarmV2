package com.slodin.transalarm;

/**
 * Created by Filipe on 2015-03-05.
 */

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Constant values reused in this Project.
 */

public final class Constants {

    private Constants() {
    }

    public static final String PACKAGE_NAME = "com.google.android.gms.location.Geofence";

    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES_NAME";

    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 2;

    /**
     * For this sample, geofences expire after twelve hours.
     */
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 700; // 1 mile, 1.6 km

    /**
     * Map for storing information about airports in the San Francisco bay area.
     */
    public static final HashMap<String, LatLng> SKYTRAIN_STATION = new HashMap<String, LatLng>();
    static {

      //***** DEBUG AND TEST GEOFENCES ******
       //home, just for testing
       SKYTRAIN_STATION.put("home", new LatLng(49.230345, -123.010826));

       //KPU
       SKYTRAIN_STATION.put("KPU", new LatLng(49.132956, -122.872196));

        /*

            ******************************************
            * ALL THE NAMES (IDS) HAVE TO BE EQUAL TO THE NAME ON THE HTML DOC,
            * BECAUSE IT WILL COMPARE AND REMOVE IT ON THE MAIN ACTIVITY,
            * AND THEN ADD IT AS THE DESTINATION

         */


        //***************** EXPO LINE ****************

        SKYTRAIN_STATION.put("waterfront", new LatLng(49.285865, -123.111816));

        SKYTRAIN_STATION.put("burrard", new LatLng(49.285819, -123.119494));

        SKYTRAIN_STATION.put("ganville", new LatLng(49.283137, -123.115753));

        SKYTRAIN_STATION.put("stadium", new LatLng(49.279370, -123.109267));

        SKYTRAIN_STATION.put("main", new LatLng(49.273356, -123.100487));

        SKYTRAIN_STATION.put("commercial", new LatLng(49.262997, -123.068445));

        SKYTRAIN_STATION.put("nanaimo", new LatLng(49.248330, -123.055922));

        SKYTRAIN_STATION.put("29th", new LatLng(49.244232, -123.046136));

        SKYTRAIN_STATION.put("joyce", new LatLng(49.238385, -123.031848));

        SKYTRAIN_STATION.put("patterson", new LatLng(49.229851, -123.012620));

        SKYTRAIN_STATION.put("metrotown", new LatLng(49.225724, -123.003798));

        SKYTRAIN_STATION.put("royaloak", new LatLng(49.220317, -122.988451));

        SKYTRAIN_STATION.put("edmonds", new LatLng(49.212158, -122.959231));

        SKYTRAIN_STATION.put("22nd", new LatLng(49.200213, -122.948971));

        SKYTRAIN_STATION.put("newwest", new LatLng(49.201463, -122.912650));

        SKYTRAIN_STATION.put("columbia", new LatLng(49.204802, -122.906129));

        SKYTRAIN_STATION.put("scottroad", new LatLng(49.204386, -122.874179));

        SKYTRAIN_STATION.put("gateway", new LatLng(49.198928, -122.850694));

        SKYTRAIN_STATION.put("surreycentral", new LatLng(49.189664, -122.847925));

        SKYTRAIN_STATION.put("kinggeorge", new LatLng(49.182835, -122.844733));



        //***************** MILLENIUM LINE ****************

        SKYTRAIN_STATION.put("renfrew", new LatLng(49.258880, -123.045327));

        SKYTRAIN_STATION.put("rupert", new LatLng(49.260839, -123.032869));

        SKYTRAIN_STATION.put("gilmore", new LatLng(49.264985, -123.013582));

        SKYTRAIN_STATION.put("brentwood", new LatLng(49.266441, -123.001802));

        SKYTRAIN_STATION.put("holdom", new LatLng(49.264756, -122.982170));

        SKYTRAIN_STATION.put("sperling", new LatLng(49.259157, -122.964012));

        SKYTRAIN_STATION.put("lakecity", new LatLng(49.254599, -122.939194));

        SKYTRAIN_STATION.put("productionway", new LatLng(49.253429, -122.918166));

        SKYTRAIN_STATION.put("lougheed", new LatLng(49.248494, -122.896931));

        SKYTRAIN_STATION.put("braid", new LatLng(49.233238, -122.882760));

        SKYTRAIN_STATION.put("sapperton", new LatLng(49.224758, -122.889444));



        //***************** WEST COAST EXPRESS ****************

        SKYTRAIN_STATION.put("portmoody", new LatLng(49.277985, -122.846392));

        SKYTRAIN_STATION.put("coquitlam", new LatLng(49.273910, -122.800063));



        //***************** SEABUS ****************

        SKYTRAIN_STATION.put("lonsdale", new LatLng(49.310115, -123.083390));
    }
}

