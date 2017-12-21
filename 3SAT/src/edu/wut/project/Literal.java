package edu.wut.project;

public class Literal {
    private String literal;
    private char variable;
    private int literalValue;
    private int variableValue;

    public Literal(String literal) {
        this.literal = literal;
        if(literal.contains("~"))
            this.variable = literal.charAt(1);
        else
            this.variable = literal.charAt(0);
    }

    public String getLiteral() {
        return literal;
    }

    public char getVariable() {
        return variable;
    }


    /* TO USE WHILE PERFORMING CHECK FORMULA SAT*/
    public void setVariableValue(int variableValue)
    {
        this.variableValue = variableValue;
        if(this.literal.contains("~") && this.variableValue == 1)
            this.literalValue = -1;
        else if(this.literal.contains("~") && this.variableValue == -1)
            this.literalValue = 1;
        else
            this.literalValue = this.variableValue;
    }
    public int getLiteralValue() {
        return literalValue;
    }

    public int getVariableValue(){
        return variableValue;
    }

    public void setLiteralValue(int literalValue) {
        this.literalValue = literalValue;
        if(this.literal.contains("~") && this.literalValue == 1)
            this.variableValue = -1;
        else if(this.literal.contains("~") && this.literalValue == -1)
            this.variableValue = 1;
        else
            this.variableValue = this.literalValue;
    }


    public int containVariable(char variable)
    {
        if(variable == this.getVariable())
            return 1;
        return -1;
    }

}
