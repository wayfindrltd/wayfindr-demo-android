package net.wayfindr.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import net.wayfindr.demo.R;
import net.wayfindr.demo.controller.DirectionsController;
import net.wayfindr.demo.controller.NearbyMessagesController;
import net.wayfindr.demo.controller.TextToSpeechController;
import net.wayfindr.demo.model.DirectionMessage;
import net.wayfindr.demo.model.Message;

import java.util.HashSet;
import java.util.Set;

public class DirectionActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_NEARBY_MESSAGES_RESOLUTION = 0;
    private TextToSpeechController textToSpeechController;
    private NearbyMessagesController nearbyMessagesController;
    private DirectionsController directionsController;
    private TextView messageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

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
    }

    private void updateCurrentMessages(Set<Message> currentMessages) {
        directionsController.setCurrentMessages(currentMessages);
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
