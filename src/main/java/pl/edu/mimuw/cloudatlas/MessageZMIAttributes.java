package pl.edu.mimuw.cloudatlas;

import pl.edu.mimuw.cloudatlas.model.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by jks on 1/4/17.
 */
public class MessageZMIAttributes extends Message {
    boolean noreply = false;
    HashMap<PathName, AttributesMap> zmiAttrs = new HashMap<>();
    HashMap<PathName, Date> zmiStamps = new HashMap<>();
    HashMap<QueryInfo, Date> installedQueries = new HashMap<>();
    HashMap<QueryInfo, Date> revokedQueries = new HashMap<>();
    ValueContact sender = null;
    MessageZMIAttributes(){

    }
    MessageZMIAttributes(Module s, Module r, int type, ValueContact tsender) {
        super(s, r, type);
        sender = tsender;
    }
}
