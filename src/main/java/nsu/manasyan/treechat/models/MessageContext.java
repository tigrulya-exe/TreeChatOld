package nsu.manasyan.treechat.models;

import java.net.InetSocketAddress;

public class MessageContext {
    private long time;

    private String address;

    private int port;

    public MessageContext(long time, InetSocketAddress inetSocketAddress) {
        this.time = time;
        this.address = inetSocketAddress.getHostString();
        this.port = inetSocketAddress.getPort();
    }
}
