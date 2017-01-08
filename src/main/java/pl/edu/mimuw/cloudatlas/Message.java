package pl.edu.mimuw.cloudatlas;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by julek on 28-Dec-16.
 */
public class Message{
    private ModuleWrapper sender;
    private ModuleWrapper recipient;
    int id;
    int messageType;
    ArrayList<Date> stamps = new ArrayList<>();
    Message(){
        
    }
    Message(Module s, Module r, int type) {
        sender = new ModuleWrapper(s);
        recipient = new ModuleWrapper(r);
        messageType = type;
        id = ThreadLocalRandom.current().nextInt();
    }

    public byte[] serialize(Kryo kryo){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Output output = new Output(stream);
        kryo.writeClassAndObject(output, this);
        output.close();
        return stream.toByteArray();
    }

    public static Message deserialize(Kryo kryo, byte[] arr){
        ByteArrayInputStream inputStream = new ByteArrayInputStream(arr);
        Input input = new Input (inputStream);
        Message m = (Message)kryo.readClassAndObject(input);
        return m;
    }

    public void stamp(){
        if(stamps.size() >= 4){
            stamps.remove(0);
        }
        stamps.add(new Date());
    }
    public void stamp(Date d){
        if(stamps.size() >= 4){
            stamps.remove(0);
        }
        stamps.add(d);
    }

    Module getRecipient(){
        return recipient.module;
    }
    Module getSender(){
        return sender.module;
    }
}
