package nsu.manasyan.treechat.util;

import java.net.InetSocketAddress;

public class Options {
    private String name;
    private int port;
    private int lossPercentage;
    private InetSocketAddress alternate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public InetSocketAddress getAlternate() {
        return alternate;
    }

    public void setAlternate(InetSocketAddress alternate) {
        this.alternate = alternate;
    }

    public int getLossPercentage() {
        return lossPercentage;
    }

    public void setLossPercentage(int lossPercentage) {
        this.lossPercentage = lossPercentage;
    }
}
