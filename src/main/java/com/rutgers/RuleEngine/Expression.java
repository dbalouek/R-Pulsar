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

public interface Expression
{
    public boolean interpret(final Map<String, ?> bindings);
}
