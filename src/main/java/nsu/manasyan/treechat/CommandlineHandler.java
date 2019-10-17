package nsu.manasyan.treechat;

import nsu.manasyan.treechat.models.MessageType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandlineHandler implements Runnable{
    private Sender sender;

    public CommandlineHandler(Sender sender) {
        this.sender = sender;
    }

    public void run(){
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String messageStr;
            while (true) {
                messageStr = reader.readLine();
                System.out.println("You: " + messageStr);
                sender.broadcastMessage(messageStr, MessageType.MESSAGE,true);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
