/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.RuleEngine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import org.cache2k.Cache;
//import org.cache2k.Cache2kBuilder;

/**
 *
 * @author eduard
 */
public class Rules {
    
    Operations operations = Operations.INSTANCE;
    private ArrayList<String> rules = new ArrayList<String>();
     private List<RuleParser> rulesp = new ArrayList<RuleParser>();
    private List<Map<String, Integer>> timeRules = new ArrayList<Map<String, Integer>>();
    private Map<String, Integer> map = new HashMap<String, Integer>();

    public void addRule(Rule r) {
        String t = r.getExpression();
        t += "--" + r.getDispatcher();
        t += "--" + r.getPriority();
        rules.add(t);
    }
    
    public void addRule(Rule r, int seconds) {
        String s = r.timeExpressions.get(0);
        map.put(s, seconds);
        timeRules.add(map);
    }
    
    public void addRule(RuleParser r) {
        rulesp.add(r);
        
        Collections.sort(rulesp, new Comparator<RuleParser>() {
        @Override
        public int compare(RuleParser o1, RuleParser o2) {
            return o1.getPriority().compareTo(o2.getPriority());
        }});
    }
    
    public boolean eval(Map<String, ?> bindings, String filename)
    {
        boolean eval = false;
        System.out.println(rulesp.size());
        for(int i = 0; i < rulesp.size(); i++) {
            for (Expression expression : rulesp.get(i).expressions)
            {
                eval = expression.interpret(bindings);
                if (eval){
                    System.out.println("Fire");
                    rulesp.get(i).dispatcher.fire(filename);
                    i = rulesp.size();
                }
            }
        }
        
        return eval;
    }
    
    public ArrayList<String> getRules() {
        return rules;
    }
    
    public List<Map<String, Integer>> getTimeRules() {
        return timeRules;
    }
    
    public Map<String, Integer> getMap() {
        return map;
    }
}
