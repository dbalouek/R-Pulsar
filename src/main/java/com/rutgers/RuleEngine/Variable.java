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

public class Variable implements Expression
{
    private String name;

    public Variable(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    @Override
    public boolean interpret(Map<String, ?> bindings)
    {
        return true;
    }
}
