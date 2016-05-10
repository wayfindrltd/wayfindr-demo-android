package net.wayfindr.demo.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

public class DirectionMessage {
    @NonNull
    public final String id;
    @NonNull
    public final Type type;
    @NonNull
    public final String message;
    @Nullable
    public final String nextId;

    public DirectionMessage(@NonNull String id, @NonNull Type type, @NonNull String message, @Nullable String nextId) {
        this.id = id;
        this.type = type;
        this.message = message;
        this.nextId = nextId;
    }

    public static DirectionMessage fromJson(String body) {
        try {
            JSONObject json = new JSONObject(body);
            String id = json.getString("id");
            Type type = Type.valueOf(json.getString("type").toUpperCase());
            String message = json.getString("message");
            String nextId = json.optString("next_id");
            return new DirectionMessage(id, type, message, nextId);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public enum Type {
        START,
        NODE,
        FINISH
    }
}
