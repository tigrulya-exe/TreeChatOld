package nsu.manasyan.treechat.models;

import java.net.InetSocketAddress;

public class NeighbourContext {
    private InetSocketAddress alternate;

    private boolean isAlive;

    public NeighbourContext(InetSocketAddress alternate) {
        this.alternate = alternate;
        this.isAlive = true;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public InetSocketAddress getAlternate() {
        return alternate;
    }
}
