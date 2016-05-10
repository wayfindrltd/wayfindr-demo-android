package net.wayfindr.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import net.wayfindr.demo.R;
import net.wayfindr.demo.controller.NearbyMessagesController;
import net.wayfindr.demo.controller.TextToSpeechController;
import net.wayfindr.demo.model.DirectionMessage;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_NEARBY_MESSAGES_RESOLUTION = 0;
    private TextToSpeechController textToSpeechController;
    private NearbyMessagesController nearbyMessagesController;
    private TextView messageTextView;
    private TextView waitingForIdTextView;
    private String waitingForId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textToSpeechController = new TextToSpeechController(this);

        nearbyMessagesController = new NearbyMessagesController(this, REQUEST_CODE_NEARBY_MESSAGES_RESOLUTION, savedInstanceState, new NearbyMessagesController.Callback() {
            @Override
            public void onNearbyMessage(DirectionMessage message) {
                onMessage(message);
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
                onMessage(new DirectionMessage("1", DirectionMessage.Type.START, "Welcome to 1. Proceed to 2", "2"));
            }
        });
        findViewById(R.id.visitMessage2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMessage(new DirectionMessage("2", DirectionMessage.Type.NODE, "Proceed to 3", "3"));
            }
        });
        findViewById(R.id.visitMessage3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMessage(new DirectionMessage("3", DirectionMessage.Type.FINISH, "You made it", null));
            }
        });
    }

    private void onMessage(DirectionMessage message) {
        messageTextView.setText(message.message);
        waitingForId = message.nextId;
        waitingForIdTextView.setText(waitingForId == null ? "" : waitingForId);
        textToSpeechController.speak(message.type == DirectionMessage.Type.FINISH ? TextToSpeechController.Earcon.JOURNEY_COMPLETE : TextToSpeechController.Earcon.GENERAL, message.message);
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
