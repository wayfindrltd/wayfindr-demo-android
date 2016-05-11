package net.wayfindr.demo.model;

public class UnknownMessage implements Message {
    private final String namespace;
    private final String type;
    private final String content;

    public UnknownMessage(String namespace, String type, String content) {
        this.namespace = namespace;
        this.type = type;
        this.content = content;
    }

    @Override
    public String toString() {
        return String.format("UnknownMessage{namespace='%s', type='%s', content='%s'}", namespace, type, content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UnknownMessage that = (UnknownMessage) o;
        return namespace.equals(that.namespace) &&
                type.equals(that.type) &&
                content.equals(that.content);
    }

    @Override
    public int hashCode() {
        int result = namespace.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + content.hashCode();
        return result;
    }
}
