package pl.edu.mimuw.cloudatlas;

import pl.edu.mimuw.cloudatlas.ZMIPicker;
import pl.edu.mimuw.cloudatlas.model.PathName;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by jks on 1/5/17.
 */
public class ZMIPickerRandom extends ZMIPicker {
    ArrayList<PathName> l = new ArrayList<>();
    ZMIPickerRandom(){
        super();
    }
    @Override
    void put2(PathName p) {
        l.add(p);
    }

    @Override
    public PathName getNext() {
        return l.isEmpty()?null:(l.get(ThreadLocalRandom.current().nextInt(l.size())));
    }
}
