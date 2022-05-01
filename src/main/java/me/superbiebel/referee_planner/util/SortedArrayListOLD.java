package me.superbiebel.referee_planner.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class SortedArrayListOLD<GameAssignment> extends ArrayList<me.superbiebel.referee_planner.domain.GameAssignment> {
    
    public final Comparator<me.superbiebel.referee_planner.domain.GameAssignment> comparator;
    
    public SortedArrayListOLD(Comparator<me.superbiebel.referee_planner.domain.GameAssignment> comparator) {
        this.comparator = comparator;
    }
    
    @Override
    public boolean add(me.superbiebel.referee_planner.domain.GameAssignment obj) {
        int index = Collections.binarySearch(this, obj, comparator);
        if (index < 0) index = ~index;
        super.add(index, obj);
        return true;
    }
    
    @Override
    public boolean addAll(Collection<? extends me.superbiebel.referee_planner.domain.GameAssignment> c) {
        c.forEach(this::add);
        return true;
    }
}
