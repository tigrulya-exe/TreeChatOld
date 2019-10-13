package nsu.manasyan.treechat;

import nsu.manasyan.treechat.models.Message;
import nsu.manasyan.treechat.models.MessageContext;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static nsu.manasyan.treechat.util.JsonService.*;

interface Handler{
    void handle(Message message, InetSocketAddress address) throws IOException;
}

public class Listener {
    // neighbour : alternate
    private int port;

    private String name;

    private Map<InetSocketAddress,InetSocketAddress> neighbours;

    private Map<Integer, Handler> handlers = new HashMap<>();

    private Map<String, MessageContext> sentMessages = new HashMap<>();

    private int BUF_LENGTH = 524288;

    private InetSocketAddress alternate;

    private DatagramSocket socket;

//    private Gson gson = new Gson();

    private byte[] receiveBuf = new byte[BUF_LENGTH];

    private ExecutorService executorService = Executors.newCachedThreadPool();

    public Listener(Map<InetSocketAddress, InetSocketAddress> neighbours, int port, String name, InetSocketAddress alternate) {
        this.neighbours = neighbours;
        this.port = port;
        this.name = name;
        this.alternate = alternate;
        initHandlers();
    }

    public void listen(){
        Message message;
        int type;
        DatagramPacket packetToReceive = new DatagramPacket(receiveBuf,BUF_LENGTH);
        try{
            socket = new DatagramSocket(port);

            //TODO tmp
            if(alternate != null){
                sendHelloMessage(alternate);
            }

            while (true) {
                socket.receive(packetToReceive);
                // TODO надо учитывать что данные в пакете могут быть больше размера json
                String jsonMsg = new String(packetToReceive.getData(), 0, packetToReceive.getLength());

                System.out.println(jsonMsg);

                message = fromJson(jsonMsg, Message.class);
                type = message.getType();
                handlers.get(type).handle(message, (InetSocketAddress) packetToReceive.getSocketAddress());
                // если это не делать то length будет равен размеру наименьшего из пришедших пакетов
                packetToReceive.setLength(BUF_LENGTH);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            socket.close();
        }
    }

    private void handleConfirmation(Message message, InetSocketAddress address){
        sentMessages.remove(message.getGUID().substring(1));
    }

    private void handleHelloMessage(Message message, InetSocketAddress senderAddress) throws IOException {
        sendConfirmation(message.getGUID(), senderAddress);
        // если это новый сосед, а не ответ на наш хелло пакет, то отправляем ему своего заместителя
        InetSocketAddress senderAlternate = fromJson(message.getContent(), InetSocketAddress.class);
        if(!neighbours.containsKey(senderAddress)){
            sendHelloMessage(senderAddress);
        }
        neighbours.put(senderAddress, senderAlternate);
    }

    private void handleMessage(Message message, InetSocketAddress senderAddress) throws IOException{
        sendConfirmation(message.getGUID(), senderAddress);
        executorService.submit(new Broadcaster(neighbours,socket,message,sentMessages));
    }

    private void sendConfirmation(String GUID, InetSocketAddress receiverAddress) throws IOException {
        Message message = new Message(name, "", 0);
        message.setGUID("A" + GUID);
        sendMessage(receiverAddress, message);
    }

    private void sendHelloMessage(InetSocketAddress receiverAddress) throws IOException {
        String alternateJson = toJson(alternate);
        Message message = new Message(name, alternateJson, 1);
        sendMessage(receiverAddress,message);
    }

    private void sendMessage(InetSocketAddress receiverAddress, Message message) throws IOException {
        byte[] buf = toJson(message).getBytes();
        //TODO тут надо учитывать, чтобы размер json не был больше буф сайза
        socket.send(new DatagramPacket(buf, buf.length, receiverAddress));
    }

    private void initHandlers(){
        handlers.put(0, this::handleConfirmation);
        handlers.put(1, this::handleHelloMessage);
        handlers.put(2, this::handleMessage);
    }
}