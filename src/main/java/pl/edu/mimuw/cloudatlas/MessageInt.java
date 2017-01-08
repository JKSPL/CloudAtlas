package pl.edu.mimuw.cloudatlas;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by jks on 1/8/17.
 */
public class MessageInt extends Message {
    int val;
    MessageInt(Module s, Module r, int type, int v) {
        super(s,r, type);
        val = v;
    }
}
