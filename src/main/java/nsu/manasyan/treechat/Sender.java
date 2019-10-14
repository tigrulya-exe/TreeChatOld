package nsu.manasyan.treechat;

import nsu.manasyan.treechat.models.Message;
import nsu.manasyan.treechat.models.MessageContext;
import nsu.manasyan.treechat.models.MessageType;
import nsu.manasyan.treechat.models.NeighbourContext;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static nsu.manasyan.treechat.util.JsonService.toJson;

public class Sender {
    private Map<InetSocketAddress, NeighbourContext> neighbours;

    private Map<String, MessageContext> sentMessages;

    private ExecutorService executorService;

    private DatagramSocket socket;

    private String name;

    private InetSocketAddress alternate;

    public Sender(Map<InetSocketAddress, NeighbourContext> neighbours, DatagramSocket socket,
                  String name, Map<String, MessageContext> sentMessages, ExecutorService executor) {
        this.neighbours = neighbours;
        this.socket = socket;
        this.name = name;
        this.sentMessages = sentMessages;
        this.executorService = executor;
    }

    public void broadcastMessage(Message message, InetSocketAddress sender, boolean isConfirmNeed) {
        executorService.submit(() ->
                neighbours.keySet().forEach(ia -> {
                    try {
                        if(ia.equals(sender))
                            return;
                        byte[] buf = toJson(message).getBytes();
                        socket.send(new DatagramPacket(buf, buf.length, ia));
                        if(isConfirmNeed)
                            sentMessages.put(message.getGUID(), new MessageContext(message, ia));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }));
    }

    public void broadcastMessage(String content, MessageType messageType, boolean isConfirmNeed) {
        broadcastMessage(new Message(name, content, messageType), null, isConfirmNeed);
    }

    public void sendMessage(InetSocketAddress receiverAddress, Message message) throws IOException {
        byte[] buf = toJson(message).getBytes();
        //TODO тут надо учитывать, чтобы размер json не был больше буф сайза
        socket.send(new DatagramPacket(buf, buf.length, receiverAddress));
    }

    public void sendConfirmation(String GUID, InetSocketAddress receiverAddress) throws IOException {
        Message message = new Message(name, "", MessageType.ACK);
        message.setGUID("A" + GUID);
        sendMessage(receiverAddress, message);
    }

    public void sendHelloMessage(InetSocketAddress receiverAddress) throws IOException {
//        String alternateJson = toJson(alternate);
        String alternateJson = (alternate == null) ? null : alternate.toString();
        Message message = new Message(name, alternateJson, MessageType.HELLO);
        sendMessage(receiverAddress,message);
    }

    public void setAlternate(InetSocketAddress alternate) throws IOException {
        this.alternate = alternate;
        sendHelloMessage(alternate);
    }
}
