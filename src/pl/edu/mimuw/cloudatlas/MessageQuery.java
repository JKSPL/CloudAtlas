package pl.edu.mimuw.cloudatlas;

import pl.edu.mimuw.cloudatlas.Message;

/**
 * Created by jks on 1/5/17.
 */
public class MessageQuery extends Message {
    QueryInfo info;
    byte[] sign;
    boolean install;
}
