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
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeOptions;

public class NearbyMessagesController {
    private final static String TAG = NearbyMessagesController.class.getSimpleName();
    private static final String NAMESPACE = "wayfindr-1303";
    private static final String TYPE = "test";
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
            public void onFound(Message message) {
                onMessage(message);
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

    private void onMessage(Message message) {
        Log.i(TAG, "Got nearby message: " + message.getNamespace() + "/" + message.getType() + ": " + message.getContent().length + " bytes");
        if (message.getNamespace().equals(NAMESPACE)) {
            if (message.getType().equals(TYPE)) {
                callback.onNearbyMessage(new String(message.getContent()));
            }
        }
    }

    public interface Callback {
        void onNearbyMessage(String message);
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
