package nsu.manasyan.treechat;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class ChatClient {
    private Map<InetSocketAddress,InetSocketAddress> neighbours = new HashMap<>();

    private String name;

    private int port;

    private InetSocketAddress alternate;

    private Listener listener;

    private CommandlineHandler commandlineHandler;

    public ChatClient(String name, int port) {
        this.name = name;
        this.port = port;
        this.listener = new Listener(neighbours,port,name,alternate);
    }

    public ChatClient(String name, int port, InetSocketAddress alternate){
        this.name = name;
        this.port = port;
        this.alternate = alternate;
        this.listener = new Listener(neighbours,port,name,alternate);
        neighbours.put(alternate,null);
    }

    public void start(){
        // other thread
//        commandlineHandler.start();
        listener.listen();
    }
}
