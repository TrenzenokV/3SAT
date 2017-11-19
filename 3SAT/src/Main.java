import javafx.util.Pair;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args)
    {
        String[] clause1 = {"~A"};
        String[] clause2 = {"A"};
        ArrayList<Literal> literals = new ArrayList<>();
        for(int i = 0; i < clause1.length; ++i)
        {
            literals.add(new Literal(clause1[i]));
        }
        Clause clauseM = new Clause(literals);

        literals.clear();
        for(int i = 0; i < clause2.length; ++i)
        {
            literals.add(new Literal(clause2[i]));
        }
        Clause clauseN = new Clause(literals);

        ArrayList<Clause> clauses = new ArrayList<>();
        clauses.add(clauseM);
        clauses.add(clauseN);

        Formula f = new Formula(clauses);

        ArrayList<Literal> partialAssignment = new ArrayList<>();
        Pair<Boolean, ArrayList<Literal>> result;
        result = f.checkSAT(partialAssignment, 0);

        System.out.println(result.getKey());
        if(result.getKey() == true)
            for(Literal l: result.getValue())
            {
                System.out.println(l.getLiteral() + " " + l.getLiteralValue() +" "+l.getVariable() + " "+l.getVariableValue() + " ");
            }
        System.out.println("FINISHED");
    }

}
