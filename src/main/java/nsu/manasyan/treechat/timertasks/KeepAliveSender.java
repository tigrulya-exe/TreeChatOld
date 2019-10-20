package nsu.manasyan.treechat.timertasks;

import nsu.manasyan.treechat.Sender;
import nsu.manasyan.treechat.models.Message;
import nsu.manasyan.treechat.models.MessageType;

import java.util.TimerTask;

public class KeepAliveSender extends TimerTask {
    private Sender sender;

    public KeepAliveSender(Sender sender) {
        this.sender = sender;
    }

    @Override
    public void run() {
        sender.broadcastMessage(new Message(MessageType.KEEP_ALIVE), null, false);
    }
}
