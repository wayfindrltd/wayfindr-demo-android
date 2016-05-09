package net.wayfindr.demo.controller;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;

import net.wayfindr.demo.R;

import java.util.HashMap;

public class TextToSpeechController {
    private final TextToSpeech textToSpeech;

    public TextToSpeechController(final Context context) {
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                for (Earcon earcon : Earcon.values()) {
                    textToSpeech.addEarcon(earcon.name(), context.getPackageName(), earcon.resource);
                }
            }
        });
    }

    public void speak(Earcon earcon, String message) {
        String utteranceId = message;
        final int result;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.playEarcon(earcon.name(), TextToSpeech.QUEUE_ADD, null);

            HashMap<String, String> params = new HashMap<>();
            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId);
            //noinspection deprecation
            result = textToSpeech.speak(message, TextToSpeech.QUEUE_ADD, params);
        } else {
            textToSpeech.playEarcon(earcon.name(), TextToSpeech.QUEUE_ADD, null, null);

            result = textToSpeech.speak(message, TextToSpeech.QUEUE_ADD, null, utteranceId);
        }
    }

    public enum Earcon {
        GENERAL(R.raw.alert_general),
        JOURNEY_COMPLETE(R.raw.alert_journey_complete);

        private final int resource;

        Earcon(int resource) {
            this.resource = resource;
        }
    }
}
