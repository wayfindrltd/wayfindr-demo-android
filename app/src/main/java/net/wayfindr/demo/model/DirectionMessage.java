package net.wayfindr.demo.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

    public enum Type {
        START,
        NODE,
        FINISH
    }
}
