package net.wayfindr.demo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import net.wayfindr.demo.R;
import net.wayfindr.demo.controller.BeaconController;
import net.wayfindr.demo.controller.TextToSpeechController;
import net.wayfindr.demo.model.Beacon;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private BeaconController beaconController;
    private TextToSpeechController textToSpeechController;
    private TextView beaconsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textToSpeechController = new TextToSpeechController(this);

        beaconsTextView = (TextView) findViewById(R.id.beacons);

        beaconController = new BeaconController(new BeaconController.Callback() {
            @Override
            public void onBeaconsChanged(List<Beacon> beacons) {
                String text = "";
                for (Beacon beacon : beacons) {
                    if (!text.isEmpty()) text += ", ";
                    text += beacon;
                }
                beaconsTextView.setText(text);
            }
        });
        beaconController.start();

        findViewById(R.id.speakFriend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeechController.speak("Hello");
            }
        });
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
        //TODO check for bluetooth and internet connection
    }

    @Override
    protected void onPause() {
        super.onPause();
        BeaconController.onActivityPause();
    }
}
