package edu.wut.project;

import javafx.util.Pair;

import java.util.ArrayList;


public class Formula {
    private ArrayList<Clause> clauses;
    private  ClauseComparator clauseComparator;
    int stopFlag = -1;
    int stopAddingFlag = 0;
    int trackFlag = 0;
    int stepTrackCounter = 0; //count how many steps algorithm made after changing trackFlag value
    Clause trackClause; // contains the clause from which the branching should be started


    public Formula(){}

    public Formula(ArrayList<Clause> clauses) {
        ArrayList<Clause> myClauses = new ArrayList<>();
        myClauses.addAll(clauses);
        this.clauses = myClauses;
        this.clauseComparator = new ClauseComparator();
        this.clauses.sort(clauseComparator);
        //this.printFormula();
    }


    private void printFormula()
    {
        for(Clause c: this.clauses)
        {
            c.printClause();
        }
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

    private Clause findClause(Clause clauseToFind)
    {
        ArrayList<Literal> toFind = clauseToFind.getLiterals();
        int returnFlag = 0;
        for(Clause c: this.clauses)
        {
            ArrayList<Literal> cLiterals = c.getLiterals();
            if(toFind.size() != cLiterals.size())
                continue;
            else
            {
                for(int i = 0; i < toFind.size(); ++i)
                {
                    if(!toFind.get(i).getLiteral().equals(cLiterals.get(i).getLiteral()))
                    {
                        returnFlag = 1;
                    }
                }
                if(returnFlag == 0)
                {
                    return c;
                }
            }
        }
        return null;
    }



    public Pair<Boolean, ArrayList<Literal>> checkSAT(ArrayList<Literal> partialAssignment, int flag)
    {

        Clause currentClause = this.firstNotSatClause();

        if(currentClause == null ) {
            stopFlag = 1;//formula is satisfiable
        }
        else if(currentClause.isEmptyClause() == 1) {
            if(trackClause.isSet() == 1)
                stopFlag = 2;//formula is NOT satisfiable
        }
        else {
            //System.out.println("BEFORE ASSIGNING");
            //currentClause.printClause();

            if(trackFlag == 1)
            {
                stepTrackCounter++;
            }

            /* First branch*/
            if(flag == 0 && stopFlag == -1)
            {
                // Setting L1 to TRUE and Adding it ti partial assignment
                int index = currentClause.getFirstNotSetLiteral();
                currentClause.getLiterals().get(index).setLiteralValue(1);
                Literal l1 = currentClause.getLiterals().get(index);
                partialAssignment.add(l1);

                flag = 0;

                if(trackFlag == 0)
                {
                    ArrayList<Literal> trackClauseLiterals = currentClause.cloneLiterals();
                    trackClause = new Clause(trackClauseLiterals);
                    trackFlag = 1;
                    //System.out.println("TRACK CLAUSE");
                    //trackClause.printClause();
                }


                this.assignValueToVariables(partialAssignment);

                //System.out.println("AFTER ASSIGNING 1ST BRANCH");
                //currentClause.printClause();
                checkSAT(partialAssignment, flag);

                //BACKTRACKING
                if((trackClause.numOfSetLiterals() == 1) && stopFlag == -1) {
                    currentClause = this.findClause(trackClause);

                    for (int i = 0; i < stepTrackCounter; ++i) {
                        int indexOfLast = partialAssignment.size() - 1;
                        partialAssignment.get(indexOfLast - i).setLiteralValue(0);
                    }

                    this.assignValueToVariables(partialAssignment);

                    for (int i = 0; i < stepTrackCounter; ++i) {
                        int indexOfLast = partialAssignment.size() - 1;
                        partialAssignment.remove(indexOfLast);
                    }

                    stepTrackCounter = 0;

                    flag = 1;

                    if(trackClause.getLiterals().size() > 1) {
                        trackClause.getLiterals().get(0).setLiteralValue(-1);
                        trackClause.getLiterals().get(1).setLiteralValue(1);
                    }

                }
                else if((trackClause.numOfSetLiterals() == 2) && stopFlag == -1)
                {

                    currentClause = this.findClause(trackClause);

                    for(int i = 0; i < stepTrackCounter; ++i)
                    {
                        int indexOfLast = partialAssignment.size() - 1;
                        partialAssignment.get(indexOfLast - i).setLiteralValue(0);
                    }

                    this.assignValueToVariables(partialAssignment);

                    for(int i = 0; i < stepTrackCounter; ++i)
                    {
                        int indexOfLast = partialAssignment.size() - 1;
                        partialAssignment.remove(indexOfLast);
                    }

                    stepTrackCounter = 0;

                    flag = 2;

                    if(trackClause.getLiterals().size() > 2) {
                        trackClause.getLiterals().get(0).setLiteralValue(-1);
                        trackClause.getLiterals().get(1).setLiteralValue(-1);
                        trackClause.getLiterals().get(2).setLiteralValue(1);
                    }
                }
                /*System.out.println("START OF 2ND BRANCH");
                currentClause.printClause();
                System.out.println("FORMULA BEFORE 2ND BRANCH");
                this.printFormula();*/
            }

            /* Second branch*/
            if(flag == 1 && stopFlag == -1)
            {

                partialAssignment.get(partialAssignment.size() - 1).setLiteralValue(-1);
                int index = currentClause.getFirstNotSetLiteral();
                if(index == -1)
                    stopFlag = 2;
                else {
                    currentClause.getLiterals().get(index).setLiteralValue(1);
                    Literal l2 = currentClause.getLiterals().get(index);
                    partialAssignment.add(l2);

                    flag = 0;

                    this.assignValueToVariables(partialAssignment);

                    //System.out.println("AFTER ASSIGNING 2ND BRANCH");
                    //currentClause.printClause();

                    checkSAT(partialAssignment, flag);

                    //System.out.println("FORMULA BEFORE 3RD BRANCH");
                    //this.printFormula();
                }
            }
            /* Third branch*/
            if(flag == 2 && stopFlag == -1)
            {

                partialAssignment.get(partialAssignment.size() - 1).setLiteralValue(-1);
                int index = currentClause.getFirstNotSetLiteral();
                if(index == -1)
                    stopFlag = 2;
                else {
                    currentClause.getLiterals().get(index).setLiteralValue(1);
                    Literal l3 = currentClause.getLiterals().get(index);
                    partialAssignment.add(l3);

                    flag = 0;

                    trackFlag = 0; //TO change trackClause

                    //System.out.println("AFTER ASSIGNING 3RD BRANCH");
                    //currentClause.printClause();

                    this.assignValueToVariables(partialAssignment);

                    checkSAT(partialAssignment, flag);
                }
            }
        }
        switch (stopFlag) {
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
