package pl.edu.mimuw.cloudatlas;

import pl.edu.mimuw.cloudatlas.model.Attribute;
import pl.edu.mimuw.cloudatlas.model.AttributesMap;
import pl.edu.mimuw.cloudatlas.model.PathName;
import pl.edu.mimuw.cloudatlas.model.Value;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by jks on 1/6/17.
 */
public class MessageZMISystemInfo extends Message {
    MessageZMISystemInfo(){
    }
    HashMap<Attribute, Value> zmiAttrs = new HashMap<>();
    MessageZMISystemInfo(Module s, Module r, int type) {
        super(s, r, type);
    }
}

