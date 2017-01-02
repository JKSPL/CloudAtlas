package pl.edu.mimuw.cloudatlas;

/**
 * Created by julek on 29-Dec-16.
 */
public class CallbackPrint implements Callback {
    @Override
    public void launch() {
        System.out.println("WORKS");
    }
}
