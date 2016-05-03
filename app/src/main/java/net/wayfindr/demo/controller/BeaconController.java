package net.wayfindr.demo.controller;

import android.app.Application;
import android.util.Log;

import com.bluecats.sdk.BCBeacon;
import com.bluecats.sdk.BCMicroLocation;
import com.bluecats.sdk.BCMicroLocationManager;
import com.bluecats.sdk.BCMicroLocationManagerCallback;
import com.bluecats.sdk.BCSite;
import com.bluecats.sdk.BlueCatsSDK;

import net.wayfindr.demo.R;

import java.util.List;
import java.util.Objects;

public class BeaconController {
    public static final String TAG = BeaconController.class.getSimpleName();

    public static void onAppCreate(Application app) {
        BlueCatsSDK.startPurringWithAppToken(app, app.getString(R.string.bluecats_app_token));
    }

    public static void onActivityResume() {
        BlueCatsSDK.didEnterForeground();
    }

    public static void onActivityPause() {
        BlueCatsSDK.didEnterBackground();
    }

    private BCSite currentSite;
    private boolean running;
    private final BCMicroLocationManager microLocationManager = BCMicroLocationManager.getInstance();
    private MicroLocationManagerCallback microLocationManagerCallback = new MicroLocationManagerCallback();

    public void start() {
        running = true;
        microLocationManager.startUpdatingMicroLocation(microLocationManagerCallback);
    }

    public void stop() {
        running = false;
        if (currentSite != null) {
            microLocationManager.stopRangingBeaconsInSite(currentSite, microLocationManagerCallback);
            currentSite = null;
        }
        microLocationManager.stopUpdatingMicroLocation(microLocationManagerCallback);
    }

    private void setSite(BCSite site) {
        if (Objects.equals(currentSite, site)) return;

        if (currentSite != null && running) {
            microLocationManager.stopRangingBeaconsInSite(currentSite, microLocationManagerCallback);
        }
        currentSite = site;

        if (currentSite != null && running) {
            microLocationManager.startRangingBeaconsInSite(currentSite, microLocationManagerCallback);
        }
    }

    private class MicroLocationManagerCallback implements BCMicroLocationManagerCallback {
        @Override
        public void onDidEnterSite(BCSite bcSite) {
            Log.d(TAG, "onDidEnterSite()");
            setSite(bcSite);
        }

        @Override
        public void onDidExitSite(BCSite bcSite) {
            Log.d(TAG, "onDidExitSite()");
            setSite(null);
        }

        @Override
        public void onDidUpdateNearbySites(List<BCSite> list) {
            Log.d(TAG, "onDidUpdateNearbySites()");
        }

        @Override
        public void onDidRangeBeaconsForSiteID(BCSite bcSite, List<BCBeacon> list) {
            Log.d(TAG, "onDidRangeBeaconsForSiteID()");
            for (BCBeacon bcBeacon : list) {
                Log.d(TAG, "" + bcBeacon.getSerialNumber() + " " + bcBeacon.getProximity().getDisplayName(false));
            }
        }

        @Override
        public void onDidUpdateMicroLocation(List<BCMicroLocation> list) {
            Log.d(TAG, "onDidUpdateMicroLocation()");
        }

        @Override
        public void didBeginVisitForBeaconsWithSerialNumbers(List<String> list) {
            Log.d(TAG, String.format("didBeginVisitForBeaconsWithSerialNumbers([%d])", list.size()));
            for (String serialNumber : list) {
                Log.d(TAG, String.format("serialNumber: %s", serialNumber));
            }
        }

        @Override
        public void didEndVisitForBeaconsWithSerialNumbers(List<String> list) {
            Log.d(TAG, String.format("didEndVisitForBeaconsWithSerialNumbers([%d])", list.size()));
            for (String serialNumber : list) {
                Log.d(TAG, String.format("serialNumber: %s", serialNumber));
            }
        }
    }
}
