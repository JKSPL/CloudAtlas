package pl.edu.mimuw.cloudatlas;

import pl.edu.mimuw.cloudatlas.model.PathName;

import java.util.HashSet;

/**
 * Created by jks on 1/5/17.
 */
public abstract class ZMIPicker {
    HashSet<PathName> paths = new HashSet<>();
    final public void put(PathName p){
        if(!paths.contains(p)){
            paths.add(p);
            put2(p);
        }
    }
    abstract void put2(PathName p);
    public abstract PathName getNext();
}
