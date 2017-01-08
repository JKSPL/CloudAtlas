package pl.edu.mimuw.cloudatlas;

import java.util.Date;
import java.util.PriorityQueue;
import java.util.Queue;

import static java.lang.Long.max;

/**
 * Created by julek on 29-Dec-16.
 */
public class ModuleTimer extends Module implements Runnable {
    public static int MSG_CALLBACK_ONCE = 1;
    public static int MSG_CALLBACK_PERIODIC = 2;
    
    public static String name = "timer";
    private static ModuleTimer instance = new ModuleTimer();
    
    Queue<MessageWithPriority> mqueue;
    Thread timerThread;
    @Override
    public void init(){
        System.out.println("inited");
        super.init();
        mqueue = new PriorityQueue<MessageWithPriority>(10, new MessageWithPriorityComparator());
        timerThread = new Thread(this);
        timerThread.start();
    }
    public static ModuleTimer getInstance(){
        return instance;
    }
    ModuleTimer() {
        super(name);
    }
    @Override
    public void receiveMessage(Message m) {
        synchronized (this){
            if(m.messageType == MSG_CALLBACK_ONCE || m.messageType == MSG_CALLBACK_PERIODIC){
                MessageCallback tm = (MessageCallback) m;
                mqueue.add(new MessageWithPriority(new Date().getTime() + tm.delay, m));
                notify();
            } else{
                debug("unrecognized message type :" + m.messageType);
            }
        }
    }

    @Override
    public void run() {
        debug("started");
        Queue<MessageWithPriority> q = new PriorityQueue<MessageWithPriority>(10, new MessageWithPriorityComparator());
        while(true){
            try {
                
                //System.out.println(mqueue == null);
                synchronized (this){
                    long currentTime = new Date().getTime();
                    while(!mqueue.isEmpty() && currentTime >= mqueue.peek().priority){
                        q.add(mqueue.remove());
                    }
                    currentTime = new Date().getTime();
                    if(q.isEmpty()){
                        if(mqueue.isEmpty()){
                            wait();
                        } else{
                            wait(max(mqueue.peek().priority - currentTime, 1L));
                        }
                    }
                };
                for(MessageWithPriority m : q){
                    ((MessageCallback)m.m).toCall.launch();
                    if(m.m.messageType == MSG_CALLBACK_PERIODIC){
                        synchronized (this){
                            mqueue.add(new MessageWithPriority(m.priority + ((MessageCallback)m.m).delay, m.m));
                        }
                    }
                }
                q.clear();
            } catch (InterruptedException e) {
                debug("finished");
                return;
            }
        }
    }
}
