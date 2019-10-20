package nsu.manasyan.treechat.timertasks;

import nsu.manasyan.treechat.Sender;
import nsu.manasyan.treechat.models.NeighbourContext;
import nsu.manasyan.treechat.util.LoggingService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.TimerTask;

public class NeighbourChecker extends TimerTask {
    private Sender sender;

    private Map<InetSocketAddress, NeighbourContext> neighbours;

    public NeighbourChecker(Map<InetSocketAddress, NeighbourContext> neighbours, Sender sender) {
        this.sender = sender;
        this.neighbours = neighbours;
    }

    @Override
    public void run() {
        InetSocketAddress alternate;

        try {
            var iter = neighbours.entrySet().iterator();
            while (iter.hasNext()) {
                var entry = iter.next();
                alternate = entry.getValue().getAlternate();

                if (!entry.getValue().isAlive()) {
                    iter.remove();
                    LoggingService.info(entry.getKey() + " is dead");
                    checkDeadNeighbour(entry.getKey());
                    if(alternate != null) {
                        neighbours.put(alternate, new NeighbourContext(null));
                        sender.sendHelloMessage(alternate);
                    }

                    checkSentMessages(entry.getKey());
                }
            }
            neighbours.forEach((k, v) -> v.setAlive(false));
        } catch (IOException e  ){
            LoggingService.error(e.getLocalizedMessage());
        }
    }

    private  void checkDeadNeighbour(InetSocketAddress address){
        if (address.equals(sender.getAlternate())){
            var newAlternate = (neighbours.isEmpty()) ? null : neighbours.keySet().iterator().next();
            LoggingService.info("New alternate: " + newAlternate);
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
