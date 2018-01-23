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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum Operations
{
    /** Application of the Singleton pattern using enum **/
    INSTANCE;

    private final Map<String, Operation> operations = new HashMap<>();
    private int NumRules = 0;
    private int NumExpression = 0;
    

    public void registerOperation(Operation op, String symbol)
    {
        if (!operations.containsKey(symbol))
            operations.put(symbol, op);
    }

    public void registerOperation(Operation op)
    {
        if (!operations.containsKey(op.getSymbol()))
            operations.put(op.getSymbol(), op);
    }

    public Operation getOperation(String symbol)
    {
        return this.operations.get(symbol);
    }

    public Set<String> getDefinedSymbols()
    {
        return this.operations.keySet();
    }
    
    public int getNumRules()
    {
        return NumRules;
    }
    
    public void setNumRules(int num) 
    {
        NumRules = num;
    }
    
    public int getNumExpression()
    {
        return NumExpression;
    }
    
    public void setNumExpression(int num) 
    {
        NumExpression = num;
    }
}
