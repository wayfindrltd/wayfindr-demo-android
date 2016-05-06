package net.wayfindr.demo.controller;

import android.app.Application;
import android.os.Handler;
import android.util.Log;

import com.bluecats.sdk.BCBeacon;
import com.bluecats.sdk.BCMicroLocation;
import com.bluecats.sdk.BCMicroLocationManager;
import com.bluecats.sdk.BCMicroLocationManagerCallback;
import com.bluecats.sdk.BCSite;
import com.bluecats.sdk.BlueCatsSDK;

import net.wayfindr.demo.R;
import net.wayfindr.demo.model.Beacon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    private final Handler handler = new Handler();
    private final Callback callback;
    private final BCMicroLocationManager microLocationManager = BCMicroLocationManager.getInstance();
    private MicroLocationManagerCallback microLocationManagerCallback = new MicroLocationManagerCallback();
    private boolean running;
    private BCSite currentSite;

    public BeaconController(Callback callback) {
        this.callback = callback;
    }

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

    private void onBeaconRanged(List<BCBeacon> list) {
        ArrayList<BCBeacon> orderedBeacons = new ArrayList<>(list);
        Collections.sort(orderedBeacons, new Comparator<BCBeacon>() {
            @Override
            public int compare(BCBeacon lhs, BCBeacon rhs) {
                double diff = rhs.getAccuracy() - lhs.getAccuracy();
                if (diff < 0) return -1;
                if (diff > 0) return 1;
                return 0;
            }
        });

        ArrayList<Beacon> beacons = new ArrayList<>();
        for (BCBeacon bcBeacon : orderedBeacons) {
            if (bcBeacon.getProximity() == BCBeacon.BCProximity.BC_PROXIMITY_UNKNOWN) continue;
            if (bcBeacon.getAccuracy() < 0.0) continue;
            beacons.add(new Beacon(bcBeacon.getSerialNumber(), bcBeacon.getMajor(), bcBeacon.getMinor()));
        }

        setBeacons(beacons);
    }

    public void setBeacons(List<Beacon> beacons) {
        for (Beacon beacon : beacons) {
            Log.d(TAG, beacon.toString());
        }
        callback.onBeaconsChanged(beacons);
    }

    public interface Callback {
        void onBeaconsChanged(List<Beacon> beacons);
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
        public void onDidRangeBeaconsForSiteID(BCSite bcSite, final List<BCBeacon> list) {
            Log.d(TAG, "onDidRangeBeaconsForSiteID()");
            for (BCBeacon bcBeacon : list) {
                Log.d(TAG, "" + bcBeacon.getSerialNumber() + " " + bcBeacon.getProximity().getDisplayName(false));
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onBeaconRanged(list);
                }
            });
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
