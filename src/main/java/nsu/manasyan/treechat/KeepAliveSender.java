package nsu.manasyan.treechat;

import nsu.manasyan.treechat.models.Message;
import nsu.manasyan.treechat.models.MessageType;
import nsu.manasyan.treechat.models.NeighbourContext;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.TimerTask;

public class KeepAliveSender extends TimerTask {
    private Sender sender;

    private Map<InetSocketAddress, NeighbourContext> neighbours;

    public KeepAliveSender(Map<InetSocketAddress, NeighbourContext> neighbours, Sender sender) {
        this.sender = sender;
        this.neighbours = neighbours;
    }

    @Override
    public void run() {
        try {
            sender.broadcastMessage(new Message(MessageType.KEEP_ALIVE), null, false);
//            sender.broadcastMessage("", MessageType.KEEP_ALIVE, false);
            var iter = neighbours.entrySet().iterator();
            while (iter.hasNext()) {
                var entry = iter.next();
                var alternate = entry.getValue().getAlternate();
                if (!entry.getValue().isAlive()) {
                    iter.remove();
                    neighbours.put(alternate, new NeighbourContext(null));
                    sender.sendHelloMessage(alternate);
                }
            }
            neighbours.forEach((k, v) -> v.setAlive(false));
        } catch (IOException e  ){

        }
    }
}
