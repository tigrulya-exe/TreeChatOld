package nsu.manasyan.treechat;

import nsu.manasyan.treechat.models.Message;
import nsu.manasyan.treechat.models.MessageContext;
import nsu.manasyan.treechat.models.MessageType;
import nsu.manasyan.treechat.models.NeighbourContext;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static nsu.manasyan.treechat.util.JsonService.*;

interface Handler{
    void handle(Message message, InetSocketAddress address) throws IOException;
}

public class Listener {
    private InetSocketAddress alternate;

    private Map<InetSocketAddress, NeighbourContext> neighbours;

    private Map<String, MessageContext> sentMessages;

    private Map<MessageType, Handler> handlers = new HashMap<>();

    private int BUF_LENGTH = 524288;

    private byte[] receiveBuf = new byte[BUF_LENGTH];

    private Sender sender;

    private DatagramSocket socket;

    private Random random = new Random();


//    private ExecutorService executorService = Executors.newCachedThreadPool();

    public Listener(Map<InetSocketAddress, NeighbourContext> neighbours,
                    Sender sender, Map<String, MessageContext> sentMessages, DatagramSocket socket) {
        this.neighbours = neighbours;
        this.sender = sender;
        sender.registerAlternateListener((a) -> alternate = a);
        this.sentMessages = sentMessages;
        this.socket = socket;
        initHandlers();
    }

    public void setAlternate(InetSocketAddress alternate) {
        this.alternate = alternate;
    }

    public void listen(){
        Message message;
        MessageType type;
        DatagramPacket packetToReceive = new DatagramPacket(receiveBuf,BUF_LENGTH);
        try{
            while (true) {
                socket.receive(packetToReceive);
                // TODO надо учитывать что данные в пакете могут быть больше размера json
                String jsonMsg = new String(packetToReceive.getData(), 0, packetToReceive.getLength());
                message = fromJson(jsonMsg, Message.class);
                type = message.getType();

                if(random.nextInt(100) < 20) {
                    System.out.println("Oopsy " + message.getGUID());
                    continue;
                }

                if(type == MessageType.HELLO)
                    System.out.println(jsonMsg);

                handlers.get(type).handle(message, (InetSocketAddress) packetToReceive.getSocketAddress());
                // если это не делать то length будет равен размеру наименьшего из пришедших пакетов
                packetToReceive.setLength(BUF_LENGTH);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        finally {
//            socket.close();
//        }
    }

    private void handleConfirmation(Message message, InetSocketAddress address){
        sentMessages.remove(message.getGUID().substring(1));
    }

    private void handleKeepAliveMessage(Message message, InetSocketAddress address){
        NeighbourContext addressContext = neighbours.get(address);
        if(addressContext != null)
            addressContext.setAlive(true);
    }

    private void handleHelloMessage(Message message, InetSocketAddress senderAddress) throws IOException {
        sender.sendConfirmation(message.getGUID(), senderAddress);
        // если это новый сосед, а не ответ на наш хелло пакет, то отправляем ему своего заместителя
        InetSocketAddress senderAlternate = getSocketAddress(message.getContent());

        if (alternate == null){
            sender.setAlternate(senderAddress);
        }

        if(!neighbours.containsKey(senderAddress)){
            sender.sendHelloMessage(senderAddress);
        }
        neighbours.put(senderAddress, new NeighbourContext(senderAlternate));
    }

    private void handleMessage(Message message, InetSocketAddress senderAddress) throws IOException{
        sender.sendConfirmation(message.getGUID(), senderAddress);
        System.out.println("[" + message.getName() + "] : " + message.getContent());
        sender.broadcastMessage(message, senderAddress, true);
    }

    private void initHandlers(){
        handlers.put(MessageType.ACK, this::handleConfirmation);
        handlers.put(MessageType.HELLO, this::handleHelloMessage);
        handlers.put(MessageType.MESSAGE, this::handleMessage);
        handlers.put(MessageType.KEEP_ALIVE, this::handleKeepAliveMessage);
    }

    private InetSocketAddress getSocketAddress(String addressAndPort){
        if (addressAndPort == null)
            return null;
        String[] tmpBuf = addressAndPort.split(":");
        return new InetSocketAddress(tmpBuf[0], Integer.parseInt(tmpBuf[1]));
    }
}