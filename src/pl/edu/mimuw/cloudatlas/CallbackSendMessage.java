package pl.edu.mimuw.cloudatlas;

import jdk.nashorn.internal.codegen.CompilerConstants;
import pl.edu.mimuw.cloudatlas.Callback;

/**
 * Created by jks on 1/2/17.
 */
public class CallbackSendMessage implements Callback {
    Message m;
    CallbackSendMessage(Message tm){
        m = tm;
    }
    @Override
    public void launch() {
        Module.sendMessage(m);
    }
}
