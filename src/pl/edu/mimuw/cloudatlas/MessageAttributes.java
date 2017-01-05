package pl.edu.mimuw.cloudatlas;

import pl.edu.mimuw.cloudatlas.model.Attribute;
import pl.edu.mimuw.cloudatlas.model.Value;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by jks on 1/4/17.
 */
public class MessageAttributes extends Message {
    HashMap<Attribute, Value> hm = new HashMap<>();
    MessageAttributes(Module s, Module r, int type) {
        super(s, r, type);
    }
}
