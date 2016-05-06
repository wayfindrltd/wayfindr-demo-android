package net.wayfindr.demo.controller;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;

import java.util.HashMap;

public class TextToSpeechController {
    private final TextToSpeech textToSpeech;

    public TextToSpeechController(Context context) {
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
            }
        });
    }

    public void speak(String message) {
        String utteranceId = message;
        final int result;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            HashMap<String, String> params = new HashMap<>();
            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId);
            //noinspection deprecation
            result = textToSpeech.speak(message, TextToSpeech.QUEUE_ADD, params);
        } else {
            result = textToSpeech.speak(message, TextToSpeech.QUEUE_ADD, null, utteranceId);
        }
    }
}
