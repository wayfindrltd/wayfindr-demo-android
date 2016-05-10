package net.wayfindr.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import net.wayfindr.demo.R;
import net.wayfindr.demo.controller.DirectionsController;
import net.wayfindr.demo.controller.NearbyMessagesController;
import net.wayfindr.demo.controller.TextToSpeechController;
import net.wayfindr.demo.model.DirectionMessage;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_NEARBY_MESSAGES_RESOLUTION = 0;
    private TextToSpeechController textToSpeechController;
    private NearbyMessagesController nearbyMessagesController;
    private DirectionsController directionsController;
    private TextView messageTextView;
    private TextView waitingForIdTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textToSpeechController = new TextToSpeechController(this);
        directionsController = new DirectionsController(textToSpeechController, new DirectionsController.Callback() {
            @Override
            public void onWaitingForIdChanged(String id) {
                waitingForIdTextView.setText(id == null ? "" : id);
            }

            @Override
            public void onMessageChanged(String message) {
                messageTextView.setText(message);
            }
        });

        nearbyMessagesController = new NearbyMessagesController(this, REQUEST_CODE_NEARBY_MESSAGES_RESOLUTION, savedInstanceState, new NearbyMessagesController.Callback() {
            @Override
            public void onNearbyMessage(DirectionMessage message) {
                directionsController.considerMessage(message);
            }
        });

        messageTextView = (TextView) findViewById(R.id.message);
        waitingForIdTextView = (TextView) findViewById(R.id.waitingForId);

        findViewById(R.id.speakGeneral).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeechController.speak(TextToSpeechController.Earcon.GENERAL, "A general message");
            }
        });
        findViewById(R.id.speakJourneyComplete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeechController.speak(TextToSpeechController.Earcon.JOURNEY_COMPLETE, "Journey is complete");
            }
        });
        findViewById(R.id.visitMessage1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                directionsController.considerMessage(new DirectionMessage("1", DirectionMessage.Type.START, "Welcome to 1. Proceed to 2", "2"));
            }
        });
        findViewById(R.id.visitMessage2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                directionsController.considerMessage(new DirectionMessage("2", DirectionMessage.Type.NODE, "Proceed to 3", "3"));
            }
        });
        findViewById(R.id.visitMessage3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                directionsController.considerMessage(new DirectionMessage("3", DirectionMessage.Type.FINISH, "You made it", null));
            }
        });
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
