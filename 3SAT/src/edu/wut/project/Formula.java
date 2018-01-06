package edu.wut.project;

import javafx.util.Pair;

import java.util.ArrayList;


public class Formula {
    private ArrayList<Clause> clauses;
    private  ClauseComparator clauseComparator;
    int stopFlagTrue = -1;
    int stopAddingFlag = 0;


    public Formula(){}

    public Formula(ArrayList<Clause> clauses) {
        ArrayList<Clause> myClauses = new ArrayList<>();
        myClauses.addAll(clauses);
        this.clauses = myClauses;
        this.clauseComparator = new ClauseComparator();
        clauses.sort(clauseComparator);
    }

    public ArrayList<Clause> getClauses() {
        return clauses;
    }

    private Clause firstNotSatClause()
    {
        for(Clause c: this.clauses) {
            if (c.isSatClause() == 0) {
                return c;
            }
        }
        return null;
    }

    private void assignValueToVariables(ArrayList<Literal> partialAssignment)
    {
        for(Clause c: clauses)
        {
            for(Literal l: partialAssignment)
            {
                if(c.containVariable(l.getVariable()) != -1)
                {
                    for(Literal cl: c.getLiterals())
                    {
                        if(cl.containVariable(l.getVariable()) == 1)
                            cl.setVariableValue(l.getVariableValue());
                    }
                }
            }
        }
    }

    private ArrayList<Literal> getNotSetLiterals()
    {
        ArrayList<Literal> toReturn = new ArrayList<>();
        for(Clause c: this.clauses)
        {
            for(Literal l: c.getLiterals())
            {
                if(l.getVariableValue() == 0 && !this.containsLiteral(toReturn,l)) {
                    l.setLiteralValue(2);
                    toReturn.add(l);
                }
            }
        }
        return toReturn;
    }

    private boolean containsLiteral(ArrayList<Literal> src, Literal literal)
    {
        for(Literal l: src)
        {
            if(l.getVariable() == literal.getVariable())
                return true;
        }
        return false;
    }
    public Pair<Boolean, ArrayList<Literal>> checkSAT(ArrayList<Literal> partialAssignment, int flag)
    {
        Clause currentClause = this.firstNotSatClause();
        if(currentClause == null ) {
            stopFlagTrue = 1;
        }
        else if(currentClause.isEmptyClause() == 1) {
            stopFlagTrue = 2;
        }
        else {
            /* First branch*/
            if(flag == 0 && stopFlagTrue == -1)
            {
                int index = currentClause.getFirstNotSetLiteral();
                currentClause.getLiterals().get(index).setLiteralValue(1);
                Literal l1 = currentClause.getLiterals().get(index);
                partialAssignment.add(l1);
                flag = 0;
                this.assignValueToVariables(partialAssignment);
                checkSAT(partialAssignment, flag);

                flag = 1;
            }
            /* Second branch*/
            if(flag == 1 && stopFlagTrue == -1)
            {
                partialAssignment.get(partialAssignment.size() - 1).setLiteralValue(0);
                int index = currentClause.getFirstNotSetLiteral();
                currentClause.getLiterals().get(index).setLiteralValue(1);
                Literal l2 = currentClause.getLiterals().get(index);
                partialAssignment.add(l2);
                flag = 0;
                this.assignValueToVariables(partialAssignment);
                checkSAT(partialAssignment, flag);
                flag = 2;
            }
            /* Third branch*/
            if(flag == 2&& stopFlagTrue == -1)
            {
                partialAssignment.get(partialAssignment.size() - 1).setLiteralValue(0);
                int index = currentClause.getFirstNotSetLiteral();
                currentClause.getLiterals().get(index).setLiteralValue(1);
                Literal l3 = currentClause.getLiterals().get(index);
                partialAssignment.add(l3);
                flag = 0;
                this.assignValueToVariables(partialAssignment);
                checkSAT(partialAssignment, flag);
            }
        }
        switch (stopFlagTrue) {
            case 1:
                if(stopAddingFlag == 0) {
                    ArrayList<Literal> arbitraryVariables = this.getNotSetLiterals();
                    partialAssignment.addAll(arbitraryVariables);
                    stopAddingFlag = -1;
                }
                return new Pair(Boolean.TRUE, partialAssignment);
            case 2:
                return new Pair(Boolean.FALSE, null);
            default:
                return null;
        }
    }

}
