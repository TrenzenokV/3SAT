package edu.wut.project;

import java.util.*;

public class FormulasGenerator {
    private int numberOfVariables;
    private int numberOfClauses;
    private int numberOfFormulas;

    private Random random;

    private List<Character> randomlyPickedVariablesList;

    public FormulasGenerator(int numberOfVariables, int numberOfClauses, int numberOfFormulas) {
        //number of variables must be 1 to 26
        this.numberOfVariables = numberOfVariables;
        //number of clauses must be >= 1
        this.numberOfClauses = numberOfClauses;
        //number of formulas must be >= 1
        this.numberOfFormulas = numberOfFormulas;

        random = new Random();

        randomlyPickedVariablesList = randomlyPickVariables(numberOfVariables);
    }

    public String[] generateFormulas() {
        ArrayList<String> generatedFormulasList = new ArrayList<>();

        for (int i = 0; i < numberOfFormulas; ++i) {
            StringBuilder sb = new StringBuilder();

            for (int j = 0; j < numberOfClauses; ++j) {
                sb.append("(");

                int numberOfLiterals = randomNumberOfLiterals();
                for (int k = 0; k < numberOfLiterals; ++k) {
                    sb.append(randomLiteral());
                    sb.append("||");
                }
                //crop last ||
                sb.setLength(sb.length() - 2);
                sb.append(")&&");
            }
            //crop last &&
            sb.setLength(sb.length() - 2);

            generatedFormulasList.add(sb.toString());
        }

        return generatedFormulasList.toArray(new String[generatedFormulasList.size()]);
    }

    private String randomLiteral() {
        Character c = randomlyPickedVariablesList.get(random.nextInt(numberOfVariables));

        if (random.nextBoolean()) {
            return "~" + c;
        } else {
            return c.toString();
        }
    }

    private int randomNumberOfLiterals() {
        //roughly 70% - 3 literals, 20% - 2 literals, 10% - 1 literal
        int r = random.nextInt(100);
        if (r < 70) {
            return 3;
        } else if (r > 90) {
            return 1;
        } else {
            return 2;
        }
    }

    private static List<Character> randomlyPickVariables(int numberOfVariables) {
        final String allVariablesString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        List<Character> list = Arrays.asList(allVariablesString
                .chars()
                .mapToObj(c -> (char)c)
                .toArray(Character[]::new)
        );

        Collections.shuffle(list);

        return list.subList(0, numberOfVariables);
    }
}
