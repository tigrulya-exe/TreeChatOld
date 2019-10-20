package nsu.manasyan.treechat;

import nsu.manasyan.treechat.models.Message;
import nsu.manasyan.treechat.models.MessageType;
import nsu.manasyan.treechat.models.NeighbourContext;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Iterator;
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

            var iter = neighbours.entrySet().iterator();
            while (iter.hasNext()) {
                var entry = iter.next();
                InetSocketAddress alternate = entry.getValue().getAlternate();
                if (!entry.getValue().isAlive()) {
                    iter.remove();
                    System.out.println(entry.getKey() + " is dead");
                    checkDeadNeighbour(entry.getKey());
                    if(alternate != null) {
                        neighbours.put(alternate, new NeighbourContext(null));
                        sender.sendHelloMessage(alternate);
                    }

                    checkSentMessages(entry.getKey());
                }
            }
            neighbours.forEach((k, v) -> v.setAlive(false));
            // TODO tmp solution
            sender.broadcastMessage(new Message(MessageType.KEEP_ALIVE), null, false);
        } catch (IOException e  ){

        }
    }

    private  void checkDeadNeighbour(InetSocketAddress address){
        if (address.equals(sender.getAlternate())){
            var newAlternate = (neighbours.isEmpty()) ? null : neighbours.keySet().iterator().next();
            System.out.println("New alternate: " + newAlternate);
            sender.setAlternate(newAlternate);
        }
    }

    private void checkSentMessages(InetSocketAddress address){
        var sentMessages = sender.getSentMessages();
        var i = sentMessages.entrySet().iterator();
        while (i.hasNext()) {
            var messageContext = i.next().getValue();
            boolean equalHost = messageContext.getHostname().equals(address.getHostName());
            boolean equalPort = messageContext.getPort() == (address.getPort());

            if(equalHost && equalPort) {
                i.remove();
            }
        }
    }

}
