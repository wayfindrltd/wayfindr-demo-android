package net.wayfindr.demo.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;

import net.wayfindr.demo.model.DirectionMessage;
import net.wayfindr.demo.model.Message;

import java.util.Set;

public class DirectionsController {
    private static final String TAG = DirectionsController.class.getSimpleName();
    private static final String INSTANCE_STATE_WAITING_FOR_ID = TAG + ".waitingForId";
    private final TextToSpeechController textToSpeechController;
    private final Callback callback;
    private String waitingForId;
    private Set<Message> currentMessages;

    public DirectionsController(TextToSpeechController textToSpeechController, @Nullable Bundle savedInstanceState, Callback callback) {
        this.textToSpeechController = textToSpeechController;
        this.callback = callback;
        this.waitingForId = (savedInstanceState == null ? null : savedInstanceState.getString(INSTANCE_STATE_WAITING_FOR_ID));
    }

    public void onSaveInstanceState(Bundle state) {
        state.putString(INSTANCE_STATE_WAITING_FOR_ID, waitingForId);
    }

    public void setCurrentMessages(Set<Message> currentMessages) {
        this.currentMessages = currentMessages;
        if (!textToSpeechController.isSpeaking()) {
            doNext();
        }
    }

    public void onTextToSpeechDone() {
        doNext();
    }

    private void doNext() {
        DirectionMessage directionMessage = getNextDirection();
        if (directionMessage != null) {
            textToSpeechController.speak(directionMessage.type == DirectionMessage.Type.FINISH ? TextToSpeechController.Earcon.JOURNEY_COMPLETE : TextToSpeechController.Earcon.GENERAL, directionMessage.message);
            waitingForId = directionMessage.nextId;
            callback.onWaitingForIdChanged(directionMessage.nextId);
        }
    }

    private DirectionMessage getNextDirection() {
        for (Message message : currentMessages) {
            if (message instanceof DirectionMessage) {
                DirectionMessage directionMessage = (DirectionMessage) message;
                if (waitingForId == null || waitingForId.isEmpty()) {
                    if (directionMessage.type == DirectionMessage.Type.START) {
                        return directionMessage;
                    }
                } else {
                    if (waitingForId.equals(directionMessage.id)) {
                        return directionMessage;
                    }
                }
            }
        }
        return null;
    }

    public String getWaitingForId() {
        return waitingForId;
    }

    public interface Callback {
        void onWaitingForIdChanged(String id);
    }
}
