/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.RuleEngine;

/**
 *
 * @author eduard
 */
import java.util.Map;
import java.util.Stack;

public abstract class Operation implements Expression
{
    protected String symbol;

    protected Expression leftOperand = null;
    protected Expression rightOperand = null;

    public Operation(String symbol)
    {
        this.symbol = symbol;
    }

    public abstract Operation copy();

    public String getSymbol()
    {
        return this.symbol;
    }

    public abstract int parse(final String[] tokens, final int pos, final Stack<Expression> stack);
    
    public abstract String parse(final String[] tokens, final int pos);

    protected Integer findNextExpression(String[] tokens, int pos, Stack<Expression> stack)
    {
        Operations operations = Operations.INSTANCE;

        for (int i = pos; i < tokens.length; i++)
        {
            Operation op = operations.getOperation(tokens[i]);
            
            if (op != null)
            {
                op = op.copy();
                // we found an operation
                i = op.parse(tokens, i, stack);

                return i;
            }
        }
        return null;
     }
}
