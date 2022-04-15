package me.superbiebel.referee_planner.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class SortedArrayList<T> extends ArrayList<T> {
    
    public final Comparator<T> comparator;
    
    public SortedArrayList(Comparator<T> comparator) {
        this.comparator = comparator;
    }
    
    @Override
    public boolean add(T obj) {
        int index = Collections.binarySearch(this, obj, comparator);
        if (index < 0) index = ~index;
        super.add(index, obj);
        return true;
    }
    
    @Override
    public boolean addAll(Collection<? extends T> c) {
        c.forEach(this::add);
        return true;
    }
}
