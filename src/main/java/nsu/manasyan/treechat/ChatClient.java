package nsu.manasyan.treechat;

import nsu.manasyan.treechat.models.MessageContext;
import nsu.manasyan.treechat.models.NeighbourContext;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatClient {
    private static final int CONFIRM_TIMEOUT_MS = 2000;

    private Map<InetSocketAddress, NeighbourContext> neighbours = new HashMap<>();

    private ExecutorService executorService = Executors.newCachedThreadPool();

    private Listener listener;

    private Sender sender;

    private CommandlineHandler commandlineHandler;

    private Map<String, MessageContext> sentMessages = new ConcurrentHashMap<>();

    private DatagramSocket socket;

    private Timer timer = new Timer();


    public ChatClient(String name, int port) throws SocketException {
        this.socket = new DatagramSocket(port);
        this.sender = new Sender(neighbours,socket, name, sentMessages, executorService);
        this.listener = new Listener(neighbours,sender,sentMessages,socket);
        this.commandlineHandler =  new CommandlineHandler(sender);
    }

    public ChatClient(String name, int port, InetSocketAddress alternate) throws IOException {
        this(name,port);
        neighbours.put(alternate,new NeighbourContext(null));
        sender.setAlternate(alternate);
        sender.notifyAlternate();
        listener.setAlternate(alternate);
    }

    public void start()  {
        executorService.submit(commandlineHandler);
        initTimer();
        listener.listen();
    }

    private void initTimer(){
        timer.schedule(new Resender(sentMessages, sender), 0, CONFIRM_TIMEOUT_MS);
        timer.schedule(new KeepAliveSender(neighbours, sender), 0, CONFIRM_TIMEOUT_MS);
    }
}
