package nsu.manasyan.treechat.models;

import java.util.UUID;

public class Message {
    private String GUID;
//    private String GUID;

    private String name;

    private String content;

    private MessageType type;

    // for json
    public Message(){}

    public Message(MessageType type){
        this.type = type;
    }

    public Message(String name, String content, MessageType type) {
        this.GUID = UUID.randomUUID().toString();
        this.name = name;
        this.content = content;
        this.type = type;
    }

    public String getGUID() {
        return GUID;
    }

    public void setGUID(String GUID) {
        this.GUID = GUID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
}
