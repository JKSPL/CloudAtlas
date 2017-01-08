package pl.edu.mimuw.cloudatlas;

import pl.edu.mimuw.cloudatlas.model.PathName;
import pl.edu.mimuw.cloudatlas.model.ValueContact;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jks on 1/5/17.
 */
public class MessageZMITimestamps extends Message {
    MessageZMITimestamps(){

    }
    Map<PathName, Date> zmiMap = new HashMap<>();
    Map<String, Date> queryMap = new HashMap<>();
    ValueContact sender;
    boolean noreply = false;
    MessageZMITimestamps(Module s, Module r, int type, ValueContact tsender) {
        super(s, r, type);
        sender = tsender;
    }

    void putZMI(PathName p, Date d){
        zmiMap.put(p, d);
    }

    void putQuery(String s, Date d){
        queryMap.put(s, d);
    }
}
