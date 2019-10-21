package nsu.manasyan.treechat;

import nsu.manasyan.treechat.models.MessageContext;
import nsu.manasyan.treechat.models.NeighbourContext;
import nsu.manasyan.treechat.timertasks.KeepAliveSender;
import nsu.manasyan.treechat.timertasks.NeighbourChecker;
import nsu.manasyan.treechat.timertasks.Resender;
import nsu.manasyan.treechat.util.Options;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatClient {
    private static final int CONFIRM_TIMEOUT_MS = 2000;

    private static final int KEEP_ALIVE_TIMEOUT_MS = 10000;

    private static final int KEEP_ALIVE_SEND_MS = 2000;

    private Map<InetSocketAddress, NeighbourContext> neighbours = new ConcurrentHashMap<>();

    private ExecutorService executorService = Executors.newCachedThreadPool();

    private Listener listener;

    private Sender sender;

    private CommandlineHandler commandlineHandler;

    private Map<String, MessageContext> sentMessages = new ConcurrentHashMap<>();

    private DatagramSocket socket;

    private Timer timer = new Timer();

    public ChatClient(Options options) throws IOException {
        this.socket = new DatagramSocket(options.getPort());
        this.sender = new Sender(neighbours,socket, options.getName(), sentMessages, executorService);
        this.listener = new Listener(neighbours,sender,sentMessages,socket, options.getLossPercentage());
        this.commandlineHandler =  new CommandlineHandler(sender);
        checkAlternate(options.getAlternate());
    }

    public void start()  {
        executorService.submit(commandlineHandler);
        initTimer();
        // we can use executorService.submit(listener); instead
        // and change listen() to run() to get possibility to stop client
        listener.listen();
    }

    public void stop(){
        timer.cancel();
        listener.interrupt();
        commandlineHandler.interrupt();
        executorService.shutdownNow();
    }

    private void initTimer(){
        timer.schedule(new Resender(sentMessages, sender), 0, CONFIRM_TIMEOUT_MS);
        timer.schedule(new KeepAliveSender(sender), 0, KEEP_ALIVE_SEND_MS);
        timer.schedule(new NeighbourChecker(neighbours, sender), 0, KEEP_ALIVE_TIMEOUT_MS);
    }

    private void checkAlternate(InetSocketAddress alternate) throws IOException {
        if(alternate == null){
            return;
        }

        neighbours.put(alternate,new NeighbourContext(null));
        sender.setAlternate(alternate);
        sender.notifyAlternate();
    }
}
