package catlas;

/**
 * Created by julek on 29-Dec-16.
 */
public class MessageWithPriority {
    Message m;
    long priority;
    MessageWithPriority(long prior, Message x){
        m = x;
        priority = prior;
    }
}
