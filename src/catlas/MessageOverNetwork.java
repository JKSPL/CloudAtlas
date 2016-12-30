package catlas;

/**
 * Created by julek on 30-Dec-16.
 */
public class MessageOverNetwork extends Message {
    Message m;
    String host;
    int port;

    MessageOverNetwork(Message tm, String thost, int tport, int ttype) {
        super(tm.getSender(), ModuleUdpSender.getInstance(), ttype);
        m = tm;
        host = thost;
        port = tport;
    }
}
