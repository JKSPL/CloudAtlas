package pl.edu.mimuw.cloudatlas;

import pl.edu.mimuw.cloudatlas.ZMIPicker;
import pl.edu.mimuw.cloudatlas.model.PathName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jks on 1/5/17.
 */
public class ZMIPickerRoundRobin extends ZMIPicker {
    List<PathName> l = new ArrayList<>();
    int i = -1;
    @Override
    void put2(PathName p) {
        l.add(p);
    }

    @Override
    public PathName getNext() {
        if(l.isEmpty()){
            return null;
        }
        i++;
        i %= l.size();
        return l.get(i);
    }
}
