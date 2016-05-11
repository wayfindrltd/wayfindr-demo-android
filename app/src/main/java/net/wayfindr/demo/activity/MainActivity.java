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
import net.wayfindr.demo.model.Message;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_NEARBY_MESSAGES_RESOLUTION = 0;
    private TextToSpeechController textToSpeechController;
    private NearbyMessagesController nearbyMessagesController;
    private DirectionsController directionsController;
    private TextView messageTextView;
    private TextView waitingForIdTextView;
    private TextView currentMessagesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textToSpeechController = new TextToSpeechController(this, new TextToSpeechController.Callback() {
            @Override
            public void onUtteranceStart(String text) {
                messageTextView.setText(text);
            }

            @Override
            public void onUtteranceDone(String text) {
                directionsController.onTextToSpeechDone();
            }
        });
        directionsController = new DirectionsController(textToSpeechController, savedInstanceState, new DirectionsController.Callback() {
            @Override
            public void onWaitingForIdChanged(String id) {
                waitingForIdTextView.setText(id == null ? "" : id);
            }
        });

        nearbyMessagesController = new NearbyMessagesController(this, REQUEST_CODE_NEARBY_MESSAGES_RESOLUTION, savedInstanceState, new NearbyMessagesController.Callback() {
            @Override
            public void onNearbyMessagesReset() {
                updateCurrentMessages(new HashSet<Message>());
            }

            @Override
            public void onNearbyMessageFound(Message message, Set<Message> currentMessages) {
                updateCurrentMessages(currentMessages);
            }

            @Override
            public void onNearbyMessageLost(Message message, Set<Message> currentMessages) {
                updateCurrentMessages(currentMessages);
            }
        });

        messageTextView = (TextView) findViewById(R.id.message);
        waitingForIdTextView = (TextView) findViewById(R.id.waitingForId);
        waitingForIdTextView.setText(directionsController.getWaitingForId());
        currentMessagesTextView = (TextView) findViewById(R.id.currentMessages);

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
//                directionsController.onMessageFound(new DirectionMessage("1", DirectionMessage.Type.START, "Welcome to 1. Proceed to 2", "2"), currentMessages);
            }
        });
        findViewById(R.id.visitMessage2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                directionsController.onMessageFound(new DirectionMessage("2", DirectionMessage.Type.NODE, "Proceed to 3", "3"), currentMessages);
            }
        });
        findViewById(R.id.visitMessage3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                directionsController.onMessageFound(new DirectionMessage("3", DirectionMessage.Type.FINISH, "You made it", null), currentMessages);
            }
        });
    }

    private void updateCurrentMessages(Set<Message> currentMessages) {
        directionsController.setCurrentMessages(currentMessages);

        if (currentMessages.isEmpty()) {
            currentMessagesTextView.setText("<None>");
        } else {
            String text = "";
            for (Message message : currentMessages) {
                if (!text.isEmpty()) text += "\n";
                text += message;
            }
            currentMessagesTextView.setText(text);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        textToSpeechController.onDestroy();
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
        directionsController.onSaveInstanceState(state);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_NEARBY_MESSAGES_RESOLUTION) {
            nearbyMessagesController.onActivityResult(requestCode, resultCode, data);
        }
    }
}
