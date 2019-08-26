/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.RuleEngine;

/**
* This class implements the = operator.
* 
* @author  Eduard Giber Renart
* @version 1.0
*/

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Equals extends Operation
{      
    private static final Operations operations = Operations.INSTANCE;
    
    public Equals()
    {
        super("=");
    }

    @Override
    public Equals copy()
    {
        return new Equals();
    }

    @Override
    public int parse(final String[] tokens, int pos, Stack<Expression> stack)
    {
        if (pos-1 >= 0 && tokens.length >= pos+1)
        {
            String var = tokens[pos-1];

            this.leftOperand = new Variable(var);
            this.rightOperand = BaseType.getBaseType(tokens[pos+1]);
            stack.push(this);
//            String exp = "exp-";
//            String rule = "rule-";
//            int r = operations.getNumRules();
//            int e = operations.getNumExpression();
//            j.lpush(rule+r, exp+e);
//            Map<String, String> hash = new HashMap<String, String>();
//            hash.put("leftOperand", var);
//            hash.put("rightOperand", tokens[pos+1]);
//            j.hmset(exp+e, hash);
//            operations.setNumExpression(e + 1);
            return pos+1;
        }
        throw new IllegalArgumentException("Cannot assign value to variable");
    }

    @Override
    public boolean interpret(Map<String, ?> bindings)
    {
        Variable v = (Variable)this.leftOperand;
        
        Object obj = bindings.get(v.getName());
        if (obj == null)
            return false;

        BaseType<?> type = (BaseType<?>)this.rightOperand;
        if (type.getType().equals(obj.getClass()))
        {
            if (type.getValue().equals(obj))
                return true;
        }
        return false;
    }

    @Override
    public String parse(String[] tokens, int pos) {
        if (pos-1 >= 0 && tokens.length >= pos+1)
        {
            String var = tokens[pos-1];
            System.out.println(var);
            if(var.equals("TUPLE_TAG")){
                var = tokens[pos+1];
                return var;
            }else {
                throw new IllegalArgumentException("Cannot assign value to variable");
            }
        }
        throw new IllegalArgumentException("Cannot assign value to variable");
    }
}
