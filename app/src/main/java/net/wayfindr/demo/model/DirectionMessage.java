package net.wayfindr.demo.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

public class DirectionMessage implements Message, Parcelable {
    @NonNull
    public final String id;
    @NonNull
    public final Type type;
    @NonNull
    public final String message;
    @Nullable
    public final String nextId;

    public static final Creator<DirectionMessage> CREATOR = new Creator<DirectionMessage>() {
        @Override
        public DirectionMessage createFromParcel(Parcel in) {
            return new DirectionMessage(in);
        }

        @Override
        public DirectionMessage[] newArray(int size) {
            return new DirectionMessage[size];
        }
    };

    public DirectionMessage(@NonNull String id, @NonNull Type type, @NonNull String message, @Nullable String nextId) {
        this.id = id;
        this.type = type;
        this.message = message;
        this.nextId = nextId;
    }

    protected DirectionMessage(Parcel in) {
        id = in.readString();
        type = Type.values()[in.readInt()];
        message = in.readString();
        nextId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(type.ordinal());
        dest.writeString(message);
        dest.writeString(nextId);
    }

    @Override
    public int describeContents() {
        return 0;
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

    @Override
    public String toString() {
        return String.format("DirectionMessage{id='%s', type=%s, message='%s', nextId='%s'}", id, type, message, nextId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DirectionMessage that = (DirectionMessage) o;
        return id.equals(that.id) && type == that.type &&
                message.equals(that.message) &&
                (nextId != null ? nextId.equals(that.nextId) : that.nextId == null);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + message.hashCode();
        result = 31 * result + (nextId != null ? nextId.hashCode() : 0);
        return result;
    }

    public enum Type {
        START,
        NODE,
        FINISH
    }
}
