package net.wayfindr.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import net.wayfindr.demo.R;
import net.wayfindr.demo.controller.BeaconController;
import net.wayfindr.demo.controller.NearbyMessagesController;
import net.wayfindr.demo.controller.TextToSpeechController;
import net.wayfindr.demo.model.Beacon;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_NEARBY_MESSAGES_RESOLUTION = 0;
    private BeaconController beaconController;
    private TextToSpeechController textToSpeechController;
    private TextView beaconsTextView;
    private NearbyMessagesController nearbyMessagesController;

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
//                beaconsTextView.setText(text);
            }
        });
//        beaconController.start();

        nearbyMessagesController = new NearbyMessagesController(this, REQUEST_CODE_NEARBY_MESSAGES_RESOLUTION, savedInstanceState, new NearbyMessagesController.Callback() {
            @Override
            public void onNearbyMessage(String message) {
                beaconsTextView.setText(message);
            }
        });

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
    protected void onStart() {
        super.onStart();
        nearbyMessagesController.onStart();
    }

    @Override
    protected void onStop() {
        nearbyMessagesController.onStop();
        super.onStop();
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

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        nearbyMessagesController.onSaveInstanceState(state);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_NEARBY_MESSAGES_RESOLUTION) {
            nearbyMessagesController.onActivityResult(requestCode, resultCode, data);
        }
    }
}
