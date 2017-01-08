package pl.edu.mimuw.cloudatlas;

import pl.edu.mimuw.cloudatlas.model.PathName;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Created by jks on 1/5/17.
 */
public class ZMIPickerRoundRobinExp extends ZMIPicker {
    PriorityQueue<PathWithPriority> pq = new PriorityQueue<>(new PathWithPriorityComparator());
    @Override
    void put2(PathName p) {
        pq.add(new PathWithPriority(p));
    }

    void restore(){
        pq.clear();
        for(PathName p: paths){
            pq.add(new PathWithPriority(p));
        }
    }

    @Override
    public PathName getNext() {
        if(pq.isEmpty()){
            return null;
        }
        PathWithPriority p = pq.poll();
        p.priority += Math.pow(p.info.getComponents().size(), 2);
        pq.add(p);
        if(p.priority > 1e9){
            restore();
        }
        return p.info;
    }
}
