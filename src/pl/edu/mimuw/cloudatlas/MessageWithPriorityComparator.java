package pl.edu.mimuw.cloudatlas;

import java.util.Comparator;

import static java.lang.Math.abs;

/**
 * Created by julek on 29-Dec-16.
 */
public class MessageWithPriorityComparator implements Comparator<MessageWithPriority>{

    @Override
    public int compare(MessageWithPriority o1, MessageWithPriority o2) {
        return Long.compare(o1.priority, o2.priority);
    }
}
