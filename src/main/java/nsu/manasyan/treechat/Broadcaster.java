package nsu.manasyan.treechat;

import nsu.manasyan.treechat.models.Message;
import nsu.manasyan.treechat.models.MessageContext;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Map;

import static nsu.manasyan.treechat.util.JsonService.toJson;

public class Broadcaster implements Runnable {
    private Map<InetSocketAddress,InetSocketAddress> neighbours;

    private Map<String, MessageContext> sentMessages;

    private DatagramSocket socket;

    private Message message;

    public Broadcaster(Map<InetSocketAddress, InetSocketAddress> neighbours, DatagramSocket socket,
                       Message message,Map<String, MessageContext> sentMessages) {
        this.neighbours = neighbours;
        this.socket = socket;
        this.message = message;
        this.sentMessages = sentMessages;
    }

    @Override
    public void run() {
        neighbours.keySet().forEach(ia -> {
            try {
                byte[] buf = toJson(message).getBytes();
                socket.send(new DatagramPacket(buf, buf.length, ia));
                sentMessages.put(message.getGUID(),new MessageContext(System.currentTimeMillis(), ia));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
