package pl.edu.mimuw.cloudatlas;

import java.util.Comparator;

/**
 * Created by jks on 1/5/17.
 */
public class PathWithPriorityComparator implements Comparator<PathWithPriority> {
    @Override
    public int compare(PathWithPriority o1, PathWithPriority o2) {
        return Long.compare(o1.priority, o2.priority);
    }
}
