package edu.wut.project;

import java.util.ArrayList;

public class Clause {
    private ArrayList<Literal> literals;

    public Clause(ArrayList<Literal> literals) {
        ArrayList<Literal> myLiterals = new ArrayList<>();
        myLiterals.addAll(literals);
        this.literals = myLiterals;
    }


    public int isSatClause()
    {
        int isSat = 0; // isSat = 1 if clause is satisfied and 0 otherwise
        for(Literal l: literals)
        {
            if(l.getLiteralValue() == 1)
                isSat = 1;
        }
        return isSat;
    }

    /* edu.wut.project.Clause is empty if all literals have false(-1) value*/
    public int isEmptyClause()
    {
        int isEmpty = 1; // isEmpty = 1 if clause is empty and 0 otherwise
        for(Literal l: literals)
        {
            if(l.getLiteralValue() == 0 || l.getLiteralValue() == 1)
                isEmpty = 0;
        }
        return isEmpty;
    }

    public ArrayList<Literal> getLiterals() {
        return literals;
    }

    public int containVariable(char variable)
    {
        for(Literal l: this.literals)
        {
            if(l.getVariable() == variable) {
                int i = this.literals.indexOf(l);
                return i;
            }
        }
        return -1;
    }

    public int getFirstNotSetLiteral()
    {
        for(Literal l: literals)
        {
            if(l.getLiteralValue() == 0)
                return literals.indexOf(l);
        }
        return -1;
    }
}
