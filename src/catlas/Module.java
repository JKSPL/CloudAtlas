package catlas;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Created by julek on 29-Dec-16.
 */
public abstract class Module {
    public final int id;
    public final String name;
    public Executor ex;
    
    public void init(){
        ex = Main.nextExecutor();
        ex.addModule(this);
    }
    
    public void init(Executor tex){
        ex = tex;
        ex.addModule(this);
    }
    public boolean enabled(){
        return ex != null;
    }
    protected Module(String tname){
        name = tname;
        id = Util.genId(name);
        Util.hmapModules.put(id, this);
    }
    public void debug(String s){
        System.out.println(name + ": " + s);
    }
    public void receiveMessage(Message m) {

    }
    static void sendMessage(Message m){
        m.getRecipient().ex.addMessage(m);
    }
}
