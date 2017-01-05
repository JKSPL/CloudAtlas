package pl.edu.mimuw.cloudatlas;

import pl.edu.mimuw.cloudatlas.model.PathName;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by jks on 1/5/17.
 */
public class ZMIPickerRandomExp extends ZMIPicker {
    ArrayList<PathWithPriority> lst = new ArrayList<>();
    long total = 0;
    @Override
    void put2(PathName p) {
        long e = (long)Math.pow(2, 10 - p.getComponents().size());
        lst.add(new PathWithPriority(p, e + total));
        total += e;
    }

    @Override
    public PathName getNext() {
        if(lst.isEmpty()){
            return null;
        }
        long a = ThreadLocalRandom.current().nextLong(total);
        int l = 0;
        int r = lst.size() - 1;
        while(l < r){
            int m = (l + r + 1) / 2;
            if(lst.get(m).priority < a){
                l = m;
            }
        }
        return lst.get(l).info;

    }
}
