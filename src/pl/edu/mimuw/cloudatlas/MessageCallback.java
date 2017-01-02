package pl.edu.mimuw.cloudatlas;

/**
 * Created by julek on 29-Dec-16.
 */
public class MessageCallback extends Message {
    Callback toCall;
    long delay;
    MessageCallback(){
        
    }
    public MessageCallback(Module s, Module r, int type, Callback c, long d) {
        super(s, r, type);
        toCall = c;
        delay = d;
    }
}
