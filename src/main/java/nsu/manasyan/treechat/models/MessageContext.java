package nsu.manasyan.treechat.models;


import java.net.InetSocketAddress;

public class MessageContext {
    private boolean isFresh;

    private int port;

    private String hostname;

    private Message message;

    public MessageContext(Message message, InetSocketAddress inetSocketAddress) {
        this.message = message;
        this.isFresh = true;
        this.hostname = inetSocketAddress.getHostString();
        this.port = inetSocketAddress.getPort();
    }

    public boolean isFresh() {
        return isFresh;
    }

    public void setFresh(boolean fresh) {
        isFresh = fresh;
    }

    public Message getMessage() {
        return message;
    }

    public int getPort() {
        return port;
    }

    public String getHostname() {
        return hostname;
    }
}
