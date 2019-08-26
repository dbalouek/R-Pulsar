/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.RuleEngine;

/**
* This class implements the OR operator.
* 
* @author  Eduard Giber Renart
* @version 1.0
*/

import java.util.Map;
import java.util.Stack;

public class Or extends Operation
{    
    public Or()
    {
        super("OR");
    }

    public Or copy()
    {
        return new Or();
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
    public boolean interpret( Map<String, ?> bindings)
    {
        System.out.println("Or");
        return leftOperand.interpret(bindings) || rightOperand.interpret(bindings);
    }

    @Override
    public String parse(String[] tokens, int pos) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}