package nsu.manasyan.treechat;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws IOException {
        ChatClient chatClient = new ChatClient("First", 8080, new InetSocketAddress("192.168.0.105", 8080));
//        ChatClient chatClient = new ChatClient("Second", 8081, new InetSocketAddress("localhost", 8080));
//        ChatClient chatClient = new ChatClient("THIRD", 8082, new InetSocketAddress("localhost", 8080));
//        ChatClient chatClient = new ChatClient("FOURTH", 8083, new InetSocketAddress("localhost", 8081));
        chatClient.start();
    }
}
