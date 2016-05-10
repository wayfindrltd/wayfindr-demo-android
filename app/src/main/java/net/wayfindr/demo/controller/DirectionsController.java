package net.wayfindr.demo.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;

import net.wayfindr.demo.model.DirectionMessage;

public class DirectionsController {
    private static final String TAG = DirectionsController.class.getSimpleName();
    private static final String INSTANCE_STATE_WAITING_FOR_ID = TAG + ".waitingForId";
    private final TextToSpeechController textToSpeechController;
    private final Callback callback;
    private String waitingForId;

    public DirectionsController(TextToSpeechController textToSpeechController, @Nullable Bundle savedInstanceState, Callback callback) {
        this.textToSpeechController = textToSpeechController;
        this.callback = callback;
        this.waitingForId = (savedInstanceState == null ? null : savedInstanceState.getString(INSTANCE_STATE_WAITING_FOR_ID));
    }

    public void onSaveInstanceState(Bundle state) {
        state.putString(INSTANCE_STATE_WAITING_FOR_ID, waitingForId);
    }

    public void considerMessage(DirectionMessage message) {
        if (waitingForId == null || waitingForId.isEmpty()) {
            if (message.type != DirectionMessage.Type.START) {
                return;
            }
        } else {
            if (!waitingForId.equals(message.id)) {
                return;
            }
        }

        textToSpeechController.speak(message.type == DirectionMessage.Type.FINISH ? TextToSpeechController.Earcon.JOURNEY_COMPLETE : TextToSpeechController.Earcon.GENERAL, message.message);
        waitingForId = message.nextId;
        callback.onWaitingForIdChanged(message.nextId);
    }

    public String getWaitingForId() {
        return waitingForId;
    }

    public interface Callback {
        void onWaitingForIdChanged(String id);
    }
}
