package edu.wut.project;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private boolean isValid;
    private ArrayList<Clause> parsedClausesList;
    private String processedString;

    public Parser(String processedString) {
        isValid = true;
        parsedClausesList = new ArrayList<>();

        setProcessedString(processedString);
        tryParse();
    }

    private void tryParse() {
        if (!isValid) {
            return;
        }

        //ensure that string has at least 3 characters - minimum length, for ex "(A)"
        if (processedString.length() < 3) {
            isValid = false;
            return;
        }

        //remove first and last parenthesis
        processedString = processedString.substring(1, processedString.length() - 1);

        String[] clausesStringsArray = processedString.split("\\)&&\\(");

        for (String clauseString : clausesStringsArray) {
            String[] allLiteralsStringsArray = clauseString.split("\\|\\|");

            String[] literalsStringsArray = new LinkedHashSet<String>(Arrays.
                    asList(allLiteralsStringsArray)).
                    toArray(new String[0]);

            ArrayList<Literal> literalsList = new ArrayList<>();

            for (String literalString : literalsStringsArray) {
                if (hasValidLiteral(literalString)) {
                    literalsList.add(new Literal(literalString));
                } else {
                    //if has non-valid literal then quit -> non-valid formula string
                    isValid = false;
                    return;
                }
            }

            Clause clause = new Clause(literalsList);
            parsedClausesList.add(clause);
        }
    }

    //static methods for checking the validity
    public static boolean containsNotAllowedCharacters(String formulaString) {
        if (formulaString == null) {
            return true;
        }
        Pattern pattern = Pattern.compile("[^A-Z|&~()]");
        Matcher matcher = pattern.matcher(formulaString);
        return matcher.find();
    }

    public static boolean hasValidLiteral(String string) {
        if (string == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("[A-Z]|~[A-Z]");
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }

    //getters && setters
    private void setProcessedString(String string) {
        if (string == null || string.isEmpty()) {
            isValid = false;
            return;
        }
        //remove whitespaces (if any)
        string = string.replaceAll("\\s+", "");

        //convert to UPPERCASE
        string = string.toUpperCase();

        //preliminary check
        if (containsNotAllowedCharacters(string)) {
            isValid = false;
            return;
        }

        this.processedString = string;
    }

    public boolean isValid() {
        return isValid;
    }

    public ArrayList<Clause> getParsedClausesList() {
        return parsedClausesList;
    }
}
