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
}
