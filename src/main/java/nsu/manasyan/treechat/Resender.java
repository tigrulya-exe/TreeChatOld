package nsu.manasyan.treechat;

import nsu.manasyan.treechat.models.MessageContext;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.TimerTask;

public class Resender extends TimerTask {

    private Map<String, MessageContext> sentMessages;

    private Sender sender;

    public Resender(Map<String, MessageContext> sentMessages, Sender sender) {
        this.sentMessages = sentMessages;
        this.sender = sender;
    }

    @Override
    public void run() {
        sentMessages.values().forEach(m -> {
            try {
                if(m.isFresh()) {
                    m.setFresh(false);
                    return;
                }
                InetSocketAddress address = new InetSocketAddress(m.getHostname(), m.getPort());
                sender.sendMessage(address, m.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }
}
