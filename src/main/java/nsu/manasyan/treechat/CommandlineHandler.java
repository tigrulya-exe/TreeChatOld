package nsu.manasyan.treechat;

import nsu.manasyan.treechat.models.MessageType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandlineHandler implements Runnable{
    private Sender sender;

    private boolean isInterrupted = false;

    public CommandlineHandler(Sender sender) {
        this.sender = sender;
    }

    public void interrupt(){
        isInterrupted = true;
    }

    public void run(){
        String name = sender.getName();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String messageStr;
            while (!isInterrupted) {
                messageStr = reader.readLine();
                System.out.println("<" + name + "> : " + messageStr);
                sender.broadcastMessage(messageStr, MessageType.MESSAGE,true);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
