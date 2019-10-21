package nsu.manasyan.treechat;

import nsu.manasyan.treechat.util.ArgResolver;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            ChatClient chatClient = new ChatClient(ArgResolver.resolve(args));
            chatClient.start();
        }catch (IOException ex){
            System.out.println(ex.getLocalizedMessage());
        }
    }
}
