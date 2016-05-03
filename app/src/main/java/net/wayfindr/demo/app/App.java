package net.wayfindr.demo.app;

import android.app.Application;

import net.wayfindr.demo.controller.BeaconController;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        BeaconController.onAppCreate(this);
    }
}
