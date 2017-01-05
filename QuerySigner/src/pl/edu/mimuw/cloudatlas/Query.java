package pl.edu.mimuw.cloudatlas;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import java.io.ByteArrayOutputStream;

/**
 * Created by jks on 1/3/17.
 */
public class Query {
    String name;
    String query;
    Query(String tn, String tq){
        name = tn;
        query =tn;
    }
    byte[] serialize(){
        Kryo kryo = new Kryo();
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        Output output = new Output(b);
        kryo.writeObject(output, this);
        output.close();
        return b.toByteArray();
    }
}
