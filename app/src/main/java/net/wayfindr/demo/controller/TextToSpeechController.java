package net.wayfindr.demo.controller;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import net.wayfindr.demo.R;

import java.util.HashMap;

public class TextToSpeechController {
    private final TextToSpeech textToSpeech;
    private final Handler handler = new Handler();

    public TextToSpeechController(final Context context, final Callback callback) {
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                for (Earcon earcon : Earcon.values()) {
                    textToSpeech.addEarcon(earcon.name(), context.getPackageName(), earcon.resource);
                }
                textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(final String utteranceId) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onTextChanged(utteranceId);
                            }
                        });
                    }

                    @Override
                    public void onDone(String utteranceId) {
                    }

                    @Override
                    public void onError(String utteranceId) {
                    }
                });
            }
        });
    }

    public void onDestroy() {
        textToSpeech.shutdown();
    }

    public void speak(Earcon earcon, String message) {
        String utteranceId = message;
        final int result;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            HashMap<String, String> params = new HashMap<>();
            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId);

            //noinspection deprecation
            textToSpeech.playEarcon(earcon.name(), TextToSpeech.QUEUE_ADD, params);
            //noinspection deprecation
            result = textToSpeech.speak(message, TextToSpeech.QUEUE_ADD, params);
        } else {
            textToSpeech.playEarcon(earcon.name(), TextToSpeech.QUEUE_ADD, null, utteranceId);
            result = textToSpeech.speak(message, TextToSpeech.QUEUE_ADD, null, utteranceId);
        }
    }

    public interface Callback {
        void onTextChanged(String text);
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
