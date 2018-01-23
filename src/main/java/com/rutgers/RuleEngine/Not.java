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

public class Not extends Operation
{    
    public Not()
    {
        super("NOT");
    }

    public Not copy()
    {
        return new Not();
    }

    @Override
    public int parse(String[] tokens, int pos, Stack<Expression> stack)
    {
        int i = findNextExpression(tokens, pos+1, stack);
        Expression right = stack.pop();

        this.rightOperand = right;
        stack.push(this);

        return i;
    }

    @Override
    public boolean interpret( Map<String, ?> bindings)
    {
        return !this.rightOperand.interpret(bindings);
    }    

    @Override
    public String parse(String[] tokens, int pos) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
