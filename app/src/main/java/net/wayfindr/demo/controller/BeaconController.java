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

public class BeaconController {
    private MicroLocationManagerCallback microLocationManagerCallback = new MicroLocationManagerCallback();

    public void start() {
        BCMicroLocationManager.getInstance().startUpdatingMicroLocation(microLocationManagerCallback);
    }

    public void stop() {
        BCMicroLocationManager.getInstance().stopUpdatingMicroLocation(microLocationManagerCallback);
    }

    public static void onAppCreate(Application app) {
        BlueCatsSDK.startPurringWithAppToken(app, app.getString(R.string.bluecats_app_token));
    }

    public static void onActivityResume() {
        BlueCatsSDK.didEnterForeground();
    }

    public static void onActivityPause() {
        BlueCatsSDK.didEnterBackground();
    }

    private static class MicroLocationManagerCallback implements BCMicroLocationManagerCallback {
        @Override
        public void onDidEnterSite(BCSite bcSite) {
            Log.d("XXX", "onDidEnterSite()");
        }

        @Override
        public void onDidExitSite(BCSite bcSite) {
            Log.d("XXX", "onDidExitSite()");
        }

        @Override
        public void onDidUpdateNearbySites(List<BCSite> list) {
            Log.d("XXX", "onDidUpdateNearbySites()");
        }

        @Override
        public void onDidRangeBeaconsForSiteID(BCSite bcSite, List<BCBeacon> list) {
            Log.d("XXX", "onDidRangeBeaconsForSiteID()");
        }

        @Override
        public void onDidUpdateMicroLocation(List<BCMicroLocation> list) {
            Log.d("XXX", "onDidUpdateMicroLocation()");
        }

        @Override
        public void didBeginVisitForBeaconsWithSerialNumbers(List<String> list) {
            Log.d("XXX", String.format("didBeginVisitForBeaconsWithSerialNumbers([%d])", list.size()));
            for (String serialNumber : list) {
                Log.d("XXX", String.format("serialNumber: %s", serialNumber));
            }
        }

        @Override
        public void didEndVisitForBeaconsWithSerialNumbers(List<String> list) {
            Log.d("XXX", String.format("didEndVisitForBeaconsWithSerialNumbers([%d])", list.size()));
            for (String serialNumber : list) {
                Log.d("XXX", String.format("serialNumber: %s", serialNumber));
            }
        }
    }

}
