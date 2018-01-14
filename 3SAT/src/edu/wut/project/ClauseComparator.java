package edu.wut.project;

import java.util.Comparator;

public class ClauseComparator implements Comparator<Clause> {

    @Override
    public int compare(Clause o1, Clause o2) {
        if(o1.getLiterals().size() > o2.getLiterals().size())
            return 1;
        else if (o1.getLiterals().size() < o2.getLiterals().size())
            return -1;
        else
            return 0;
    }
}
