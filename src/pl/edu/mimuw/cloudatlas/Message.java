package pl.edu.mimuw.cloudatlas;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by julek on 28-Dec-16.
 */
public class Message{
    private ModuleWrapper sender;
    private ModuleWrapper recipient;
    int id;
    int messageType;
    Message(){
        
    }
    Message(Module s, Module r, int type) {
        sender = new ModuleWrapper(s);
        recipient = new ModuleWrapper(r);
        messageType = type;
        id = ThreadLocalRandom.current().nextInt();
    }
    
    Module getRecipient(){
        return recipient.module;
    }
    Module getSender(){
        return sender.module;
    }
}
