package pl.edu.mimuw.cloudatlas;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import pl.edu.mimuw.cloudatlas.model.PathName;

import java.io.ByteArrayOutputStream;

/**
 * Created by jks on 1/3/17.
 */
public class QueryInfo {
    String name;
    String query;
    QueryInfo(String tn, String tq){
        name = tn;
        query = tq;
    }
    byte[] serialize(){
        Kryo kryo = new Kryo();
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        Output output = new Output(b);
        kryo.writeObject(output, this);
        output.close();
        return b.toByteArray();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        return name.equals(((QueryInfo)obj).name) &&
                query.equals(((QueryInfo)obj).query);
    }

    @Override
    public int hashCode(){
        return (name + ";" + query + "@").hashCode();
    }
}
