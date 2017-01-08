package pl.edu.mimuw.cloudatlas;

import pl.edu.mimuw.cloudatlas.model.PathName;
import pl.edu.mimuw.cloudatlas.model.ZMI;

/**
 * Created by jks on 1/5/17.
 */
public class PathWithPriority {
    PathName info;
    long priority;
    PathWithPriority(PathName tinfo){
        info = tinfo;
        priority = 0;
    }
    PathWithPriority(PathName tinfo, long tp){
        info = tinfo;
        priority = tp;
    }
}
