/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.RuleEngine;

/**
* This class implements the AND operator.
* 
* @author  Eduard Giber Renart
* @version 1.0
*/

import java.util.List;
import java.util.Map;
import java.util.Stack;

public class And extends Operation
{    
    private static final Operations operations = Operations.INSTANCE;
    
    public And()
    {
        super("AND");
    }

    public And copy()
    {
        return new And();
    }

    @Override
    public int parse(String[] tokens, int pos, Stack<Expression> stack)
    {
        Expression left = stack.pop();
        int i = findNextExpression(tokens, pos+1, stack);
        Expression right = stack.pop();

        this.leftOperand = left;
        this.rightOperand = right;

        stack.push(this);

        return i;
    }

    @Override
    public boolean interpret(Map<String, ?> bindings)
    {        
        return leftOperand.interpret(bindings) && rightOperand.interpret(bindings);
    }

    @Override
    public String parse(String[] tokens, int pos) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}