package catlas;

import com.esotericsoftware.kryo.Kryo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.*;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Int;

/**
 * Created by julek on 28-Dec-16.
 */
public class Executor implements Runnable {
    Kryo kryo;
    int id;
    BlockingQueue<Message> messageQueue;
    HashMap<Integer, Module> hmap;
    public Executor(int id1){
        id = id1;
        kryo = new Kryo();
        hmap = new HashMap<Integer, Module>();
        messageQueue = new LinkedBlockingQueue<>();
    }
    public void run(){
        System.out.println("Executor nr " + Integer.toString(id) + " starting");
        while(true){
            try {
                Message m = messageQueue.take();
                assert m.getRecipient().ex != null;
                System.out.println("Executor nr " + id + " giving message to " + m.getRecipient().name);
                m.getRecipient().receiveMessage(m);
            } catch (InterruptedException e) {
                System.out.println("Executor nr " + Integer.toString(id) + " finished");
                return;
            }
        }
    }
    public void addModule(Module m){
        System.out.println("Executor nr " + Integer.toString(id) + " adds module: " + m.name);
        hmap.put(m.id, m);
    }
    
    public void addMessage(Message m){
        messageQueue.add(m);
    }
    
}
