package net.wayfindr.demo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import net.wayfindr.demo.R;
import net.wayfindr.demo.controller.BeaconController;

public class MainActivity extends AppCompatActivity {
    private BeaconController beaconController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        beaconController = new BeaconController();
        beaconController.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconController.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BeaconController.onActivityResume();

        // TODO request location permission
    }

    @Override
    protected void onPause() {
        super.onPause();
        BeaconController.onActivityPause();
    }
}
