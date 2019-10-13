package nsu.manasyan.treechat;

import nsu.manasyan.treechat.models.MessageContext;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatClient {
    private Map<InetSocketAddress,InetSocketAddress> neighbours = new HashMap<>();

    private ExecutorService executorService = Executors.newCachedThreadPool();

    private Listener listener;

    private Sender sender;

    private CommandlineHandler commandlineHandler;

    private Map<String, MessageContext> sentMessages = new HashMap<>();

    public ChatClient(String name, int port) throws SocketException {
        DatagramSocket socket = new DatagramSocket(port);
        this.sender = new Sender(neighbours,socket, name, sentMessages, executorService);
        this.listener = new Listener(neighbours,port,sender,sentMessages,socket);
        this.commandlineHandler =  new CommandlineHandler(sender);
    }

    public ChatClient(String name, int port, InetSocketAddress alternate) throws IOException {
        this(name,port);
        neighbours.put(alternate,null);
        sender.setAlternate(alternate);
    }

    public void start()  {
        executorService.submit(commandlineHandler);
        listener.listen();
    }
}
