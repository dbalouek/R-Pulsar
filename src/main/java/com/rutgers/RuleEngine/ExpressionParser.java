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
import java.util.Stack;

public class ExpressionParser
{
    private static final Operations operations = Operations.INSTANCE;

    public static Expression fromString(String expr)
    {
        Stack<Expression> stack = new Stack<>();

        String[] tokens = expr.split("\\s");
        
        for (int i=0; i < tokens.length-1; i++)
        {
            Operation op = operations.getOperation(tokens[i]);
            if ( op != null )
            {
                // create a new instance
                op = op.copy();
                i = op.parse(tokens, i, stack);
            }
        }
        int n = operations.getNumRules();
        operations.setNumRules(n+1);
        return stack.pop();
    }
    
    public static String fromTimeString(String expr)
    {

        String[] tokens = expr.split("\\s");
        String t = "";
        
        for (int i=0; i < tokens.length-1; i++)
        {
            Operation op = operations.getOperation(tokens[i]);
            if ( op != null )
            {
                // create a new instance
                op = op.copy();
                t = op.parse(tokens, i);
                System.out.println(t);
            }
        }
        int n = operations.getNumRules();
        operations.setNumRules(n+1);
        return t;
    }
}
