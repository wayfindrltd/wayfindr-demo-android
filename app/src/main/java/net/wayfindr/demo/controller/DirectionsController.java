package net.wayfindr.demo.controller;

import net.wayfindr.demo.model.DirectionMessage;

public class DirectionsController {
    private final TextToSpeechController textToSpeechController;
    private final Callback callback;
    private String waitingForId;

    public DirectionsController(TextToSpeechController textToSpeechController, Callback callback) {
        this.textToSpeechController = textToSpeechController;
        this.callback = callback;
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

        callback.onMessageChanged(message.message);
        callback.onWaitingForIdChanged(message.nextId);
    }

    public interface Callback {
        void onWaitingForIdChanged(String id);

        void onMessageChanged(String message);
    }
}
