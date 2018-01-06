package edu.wut.project;

import javafx.util.Pair;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        //testWithTimeMeasurements();

        if (args.length == 0) {
            menu();
        } else {
            solveFormulasInFiles(args);
        }
    }

    public static void testWithTimeMeasurements() {
        int[] nValues = new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                11, 12, 13, 14, 15, 16, 17, 18, 19,
                20, 21, 22, 23, 24, 25, 26};

        measureTime(nValues, 500);
    }

    private static final String generatedFileDefaultFilename = "generated.txt";
    private static final String helpString =
            "Enter \"-s filename...\" to solve formulas in file(s)\n" +
            "   At least one filename required\n" +
            "   The results are stored in file(s) named \"results.filename\"\n" +
            "       Example: -s file.txt (Results are stored in \"results.file.txt\")\n" +
            "       Example: -s file1 file2 file3 (Results are stored in \"results.file1\", \"results.file2\" and \"results.file3\" respectively)\n" +
            "Enter \"-g numberOfVariables numberOfClauses numberOfFormulas [outputFilename]\" to generate example file with specified number of variables and formulas\n" +
            "   Number of variables can be 1 to 26 (letters in modern English alphabet)\n" +
            "   Number of clauses is a number of clauses in single formula. Must be at least 1\n" +
            "   Number of formulas is a number of formulas in file. Must be at least 1\n" +
            "   Optionally you can specify output filename (default is \""+ generatedFileDefaultFilename + "\")\n" +
            "       Example: -g 3 5 50\n" +
            "       Example: -g 5 3 100 customFilename.txt\n" +
            "Enter \"exit\" to exit\n" +
            "Enter \"help\" to view this help";

    public static void menu() {
        System.out.println("Welcome!");
        System.out.println(helpString);

        Scanner scanner = new Scanner(System.in);

        for (String inputString = scanner.nextLine().trim();
             !inputString.toLowerCase().equals("exit");
             inputString = scanner.nextLine().trim()) {

            if (inputString.isEmpty()) {
                System.out.println("Entered empty string! Enter \"help\" to view help");
                continue;
            }

            if (inputString.toLowerCase().equals("help")) {
                System.out.println(helpString);
                continue;
            }

            if (inputString.toLowerCase().startsWith("-g ")) {
                String[] tokens = inputString.substring(2).trim().split("\\s+");

                if (tokens.length < 3) {
                    System.out.println("Too few arguments to -g command!");
                    continue;
                }
                if (tokens.length > 4) {
                    System.out.println("Too many arguments to -g command!");
                    continue;
                }

                String generateFilename = generatedFileDefaultFilename;
                if (tokens.length == 4) {
                    generateFilename = tokens[3];
                }

                try {
                    int numberOfVariables = Integer.parseInt(tokens[0]);
                    int numberOfClauses = Integer.parseInt(tokens[1]);
                    int numberOfFormulas = Integer.parseInt(tokens[2]);

                    if (numberOfVariables < 1 || numberOfVariables > 26) {
                        System.out.println("Number of variables must be from 1 to 26!");
                        continue;
                    }

                    if (numberOfClauses < 1) {
                        System.out.println("Number of clauses must be at least 1!");
                        continue;
                    }

                    if (numberOfFormulas < 1) {
                        System.out.println("Number of formulas must be at least 1!");
                        continue;
                    }

                    generateFormulasFile(numberOfVariables, numberOfClauses, numberOfFormulas, generateFilename);

                } catch (NumberFormatException e) {
                    System.out.println("Inputted numbers are incorrect!");
                }

                continue;
            }

            if (inputString.toLowerCase().startsWith("-s ")) {
                String[] filenames = inputString.substring(2).trim().split("\\s+");

                solveFormulasInFiles(filenames);

                continue;
            }

            System.out.println("Invalid input! Enter \"help\" to view help");
        }
        System.out.println("Exiting...");
    }

    public static void generateFormulasFile(int numberOfVariables, int numberOfClauses, int numberOfFormulas, String filename) {
        FormulasGenerator formulasGenerator = new FormulasGenerator(numberOfVariables, numberOfClauses, numberOfFormulas);

        String[] formulas = formulasGenerator.generateFormulas();

        try (PrintWriter printWriter = new PrintWriter(filename)) {
            for (String formulaString : formulas) {
                printWriter.println(formulaString);
            }
            System.out.println("Generated example file named \"" + filename + "\"");
        } catch (FileNotFoundException e) {
            System.out.println("Cannot write to \"" + filename + "\"! It may be read-only or file can't be created");
        }
    }

    public static void solveFormulasInFiles(String[] filenames) {
        for (String filename : filenames) {
            try (Stream<String> stringStream = Files.lines(Paths.get(filename))) {
                String[] lines = stringStream.toArray(String[]::new);
                ArrayList<String> resultLines = new ArrayList<>();

                for (String lineString : lines) {
                    String resultLine = "";

                    Parser parser = new Parser(lineString);

                    if (parser.isValid()) {
                        //if successfully parsed clauses from formula string
                        Formula formula = new Formula(parser.getParsedClausesList());

                        ArrayList<Literal> partialAssignment = new ArrayList<>();
                        Pair<Boolean, ArrayList<Literal>> result;
                        result = formula.checkSAT(partialAssignment, 0);

                        if (result.getKey() == true) {
                            //formula is satisfiable
                            StringBuilder sb = new StringBuilder();
                            sb.append("YES ");

                            for (Literal literal : result.getValue()) {
                                int variableValue = literal.getVariableValue();

                                if (variableValue == 2) {
                                    sb.append(literal.getVariable()).append("=").append("-1 OR 1; ");
                                } else {
                                    sb.append(literal.getVariable()).append("=").append(literal.getVariableValue());
                                    sb.append("; ");
                                }
                            }
                            //crop last unneeded space
                            sb.setLength(sb.length() - 2);

                            resultLine = sb.toString();
                        } else {
                            //formula is NOT satisfiable
                            resultLine = "NO";
                        }
                    } else {
                        //parse error
                        resultLine = "Incorrect formula!";
                    }

                    resultLines.add(resultLine);
                }

                //write results lines into file
                try (PrintWriter printWriter = new PrintWriter("result." + filename)) {
                    for (String resultLine : resultLines) {
                        printWriter.println(resultLine);
                    }
                }
                System.out.println("Results stored in file named \"result." + filename + "\"");
            } catch (NoSuchFileException e) {
                System.out.println("File named \"" + filename + "\" was not found!");
            } catch (FileNotFoundException e) {
                System.out.println("Cannot write to \"result." + filename + "\"! It may be read-only or file can't be created");
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    public static void measureTime(int[] nValues, int clausesQuantity) {
        int numberOfFormulas = 1000;

        ArrayList<String> filenames = new ArrayList<>();

        for (int n : nValues) {
            String filename = "TM_formulas_N=" + n + "_CL=" + clausesQuantity + ".txt";
            filenames.add(filename);
            generateFormulasFile(n, clausesQuantity, numberOfFormulas, filename);
        }

        for (int i = 0; i < nValues.length; ++i) {
            try (Stream<String> stringStream = Files.lines(Paths.get(filenames.get(i)))) {
                String[] lines = stringStream.toArray(String[]::new);
                ArrayList<String> resultLines = new ArrayList<>();

                ArrayList<Long> timeValues = new ArrayList<>(); //for time

                for (String lineString : lines) {
                    String resultLine = "";

                    Parser parser = new Parser(lineString);

                    if (parser.isValid()) {
                        //if successfully parsed clauses from formula string
                        Formula formula = new Formula(parser.getParsedClausesList());

                        ArrayList<Literal> partialAssignment = new ArrayList<>();
                        Pair<Boolean, ArrayList<Literal>> result;

                        long startTime = System.nanoTime(); //for time

                        result = formula.checkSAT(partialAssignment, 0);

                        long endTime = System.nanoTime(); //for time
                        long elapsedTime = endTime - startTime; //for time

                        timeValues.add(elapsedTime);

                        if (result.getKey() == true) {
                            //formula is satisfiable
                            StringBuilder sb = new StringBuilder();
                            sb.append("YES ");

                            for (Literal literal : result.getValue()) {
                                int variableValue = literal.getVariableValue();

                                if (variableValue == 2) {
                                    sb.append(literal.getVariable()).append("=").append("-1 OR 1; ");
                                } else {
                                    sb.append(literal.getVariable()).append("=").append(literal.getVariableValue());
                                    sb.append("; ");
                                }
                            }
                            //crop last unneeded space
                            sb.setLength(sb.length() - 2);

                            resultLine = sb.toString();
                        } else {
                            //formula is NOT satisfiable
                            resultLine = "NO";
                        }
                    } else {
                        //parse error
                        resultLine = "Incorrect formula!";
                    }

                    resultLines.add(resultLine);
                }

                //write results lines into file
                try (PrintWriter printWriter = new PrintWriter(filenames.get(i).replaceAll("formulas", "results"))) {
                    for (String resultLine : resultLines) {
                        printWriter.println(resultLine);
                    }
                }
                //write time values into file
                try (PrintWriter printWriter = new PrintWriter(filenames.get(i).replaceAll("formulas", "times"))) {
                    for (Long timeValue : timeValues) {
                        printWriter.println(nValues[i] + " " + timeValue);
                    }
                }
            } catch (NoSuchFileException e) {
                System.out.println("File named \"" + filenames.get(i) + "\" was not found!");
            } catch (FileNotFoundException e) {
                System.out.println("Cannot write to \"result." + filenames.get(i) + "\"! It may be read-only or file can't be created");
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}
