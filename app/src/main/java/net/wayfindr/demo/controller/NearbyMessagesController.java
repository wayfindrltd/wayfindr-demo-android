package net.wayfindr.demo.controller;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeOptions;

import net.wayfindr.demo.model.DirectionMessage;
import net.wayfindr.demo.model.Message;
import net.wayfindr.demo.model.UnknownMessage;

import java.util.Arrays;

public class NearbyMessagesController {
    private final static String TAG = NearbyMessagesController.class.getSimpleName();
    private static final String NAMESPACE = "wayfindr-1303";
    private static final String TYPE = "direction";
    private static final String INSTANCE_STATE_RESOLVING_ERROR = TAG + ".resolvingError";
    private final Activity activity;
    private final int requestNearbyMessagesResolution;
    private final Callback callback;
    private final GoogleApiClient googleApiClient;
    private boolean resolvingError;

    public NearbyMessagesController(Activity activity, int requestNearbyMessagesResolution, @Nullable Bundle savedInstanceState, Callback callback) {
        this.activity = activity;
        this.requestNearbyMessagesResolution = requestNearbyMessagesResolution;
        this.callback = callback;
        resolvingError = savedInstanceState != null && savedInstanceState.getBoolean(INSTANCE_STATE_RESOLVING_ERROR);

        GoogleApiCallbacks callbacks = new GoogleApiCallbacks();
        googleApiClient = new GoogleApiClient.Builder(this.activity)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(callbacks)
                .addOnConnectionFailedListener(callbacks)
                .build();
    }

    public void onStart() {
        googleApiClient.connect();
    }

    public void onStop() {
        googleApiClient.disconnect();
    }

    private void subscribe() {
        SubscribeOptions options = new SubscribeOptions.Builder()
                .setStrategy(Strategy.BLE_ONLY)
                .build();

        Nearby.Messages.subscribe(googleApiClient, new MessageListener() {
            @Override
            public void onFound(com.google.android.gms.nearby.messages.Message message) {
                onMessageFound(parseMessage(message));
            }

            @Override
            public void onLost(com.google.android.gms.nearby.messages.Message message) {
                onMessageLost(parseMessage(message));
            }
        }, options)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (!status.isSuccess()) {
                            onNearbySubscriptionFailure(status);
                        }
                    }
                });
    }

    private void onNearbySubscriptionFailure(Status status) {
        if (status.hasResolution()) {
            if (!resolvingError) {
                try {
                    status.startResolutionForResult(activity, requestNearbyMessagesResolution);
                    resolvingError = true;
                } catch (IntentSender.SendIntentException exception) {
                    Log.e(TAG, "Failed starting resolution", exception);
                }
            }
        }
    }

    public void onSaveInstanceState(Bundle state) {
        state.putBoolean(INSTANCE_STATE_RESOLVING_ERROR, resolvingError);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == requestNearbyMessagesResolution) {
            resolvingError = false;

            Log.i(TAG, "Result from error resolution: " + resultCode);
            if (resultCode == Activity.RESULT_OK) {
                subscribe();
            }
        }
    }

    private void onMessageFound(Message message) {
        Log.i(TAG, "Found nearby message: " + message);
        callback.onNearbyMessageFound(message);
    }

    private void onMessageLost(Message message) {
        Log.i(TAG, "Lost nearby message: " + message);
        callback.onNearbyMessageLost(message);
    }

    private Message parseMessage(com.google.android.gms.nearby.messages.Message message) {
        String body;
        try {
            body = new String(message.getContent());
        } catch (Exception e) {
            body = Arrays.toString(message.getContent());
        }
        if (message.getNamespace().equals(NAMESPACE) && message.getType().equals(TYPE)) {
            return DirectionMessage.fromJson(body);
        } else {
            return new UnknownMessage(message.getNamespace(), message.getType(), body);
        }
    }

    public interface Callback {
        void onNearbyMessageFound(Message message);

        void onNearbyMessageLost(Message message);
    }

    private class GoogleApiCallbacks implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Log.i(TAG, "Connected to Google API, subscribe to nearby messages");
            subscribe();
        }

        @Override
        public void onConnectionSuspended(int cause) {
            Log.w(TAG, "Google API connection suspended: " + cause);
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.w(TAG, "Google API connection failed: " + connectionResult.getErrorMessage());
        }
    }
}
