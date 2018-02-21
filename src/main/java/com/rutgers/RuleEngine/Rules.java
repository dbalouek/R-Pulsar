/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.RuleEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author eduard
 */
public class Rules {
    
    Operations operations = Operations.INSTANCE;
    private List<Rule> rules = new ArrayList<Rule>();    

    public void addRule(Rule r) {
        rules.add(r);
        
        Collections.sort(rules, new Comparator<Rule>() {
        @Override
        public int compare(Rule o1, Rule o2) {
            return o1.getPriority().compareTo(o2.getPriority());
        }});
    }
    
    public boolean eval(Map<String, ?> bindings)
    {
        boolean eval = false;
        
        for(int i = 0; i < rules.size(); i++) {
            for (Expression expression : rules.get(i).expressions)
            {
                eval = expression.interpret(bindings);
                if (eval){
                    rules.get(i).dispatcher.fire();
                    i = rules.size();
                }
            }
        }
    
        return eval;
    }
}
