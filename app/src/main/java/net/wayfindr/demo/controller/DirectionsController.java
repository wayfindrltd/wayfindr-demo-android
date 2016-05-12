package net.wayfindr.demo.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;

import net.wayfindr.demo.model.DirectionMessage;
import net.wayfindr.demo.model.Message;

import java.util.Set;

public class DirectionsController {
    private static final String TAG = DirectionsController.class.getSimpleName();
    private static final String INSTANCE_STATE_CURRENT_MESSAGE = TAG + ".currentMessage";
    private static final String INSTANCE_STATE_FINISHED = TAG + ".finished";
    private final TextToSpeechController textToSpeechController;
    private final Callback callback;
    private DirectionMessage currentMessage;
    private Set<Message> currentMessages;
    private boolean finished;

    public DirectionsController(TextToSpeechController textToSpeechController, @Nullable Bundle savedInstanceState, Callback callback) {
        this.textToSpeechController = textToSpeechController;
        this.callback = callback;
        currentMessage = (savedInstanceState == null ? null : (DirectionMessage) savedInstanceState.getParcelable(INSTANCE_STATE_CURRENT_MESSAGE));
        finished = (savedInstanceState != null && savedInstanceState.getBoolean(INSTANCE_STATE_FINISHED, false));
    }

    public void onSaveInstanceState(Bundle state) {
        state.putParcelable(INSTANCE_STATE_CURRENT_MESSAGE, currentMessage);
        state.putBoolean(INSTANCE_STATE_FINISHED, finished);
    }

    public boolean isFinished() {
        return finished;
    }

    public void restart() {
        currentMessage = null;
        finished = false;
        doNext();
    }

    public void setCurrentMessages(Set<Message> currentMessages) {
        this.currentMessages = currentMessages;
        if (!textToSpeechController.isSpeaking()) {
            doNext();
        }
    }

    public void onTextToSpeechDone() {
        if (currentMessage != null && currentMessage.type == DirectionMessage.Type.FINISH) {
            finished = true;
            currentMessage = null;
            callback.onJourneyFinished();
        } else {
            doNext();
        }
    }

    private void doNext() {
        if (finished) return;

        DirectionMessage directionMessage = getNextDirection();
        if (directionMessage != null) {
            currentMessage = directionMessage;
            textToSpeechController.speak(directionMessage.type == DirectionMessage.Type.FINISH ? TextToSpeechController.Earcon.JOURNEY_COMPLETE : TextToSpeechController.Earcon.GENERAL, directionMessage.message);
            callback.onWaitingForIdChanged(directionMessage.nextId);
        }
    }

    private DirectionMessage getNextDirection() {
        String waitingForId = getWaitingForId();
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
        return currentMessage == null ? null : currentMessage.nextId;
    }

    public interface Callback {
        void onWaitingForIdChanged(String id);

        void onJourneyFinished();
    }
}
