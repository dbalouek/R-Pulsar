/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.RuleEngine;

/**
* This class implements the > operator.
* 
* @author  Eduard Giber Renart
* @version 1.0
*/
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class GreaterThan extends Operation
{      
    private static final Operations operations = Operations.INSTANCE;

    public GreaterThan()
    {
        super(">");
    }

    @Override
    public GreaterThan copy()
    {
        return new GreaterThan();
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
        System.out.println("GraterThan");
        Variable v = (Variable)this.leftOperand;
        Object obj = bindings.get(v.getName());
        if (obj == null)
            return false;

        BaseType<?> type = (BaseType<?>)this.rightOperand;
        
        int right = Integer.valueOf(type.getValue().toString());
        int left = Integer.valueOf(obj.toString());
        
        return left > right;
    }

    @Override
    public String parse(String[] tokens, int pos) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
