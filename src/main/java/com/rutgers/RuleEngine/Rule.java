/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.RuleEngine;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author eduard
 */
public class Rule 
{
    public String expressions;
    public List<String> timeExpressions;
    public String dispatcher;
    public Integer priority;

    public static class Builder
    {
        private List<String> timeExpressions = new ArrayList<>();
        private String expressions;
        private String dispatcher;
        private Integer priority;

//        public Builder withExpression(Expression expr)
//        {
//            expressions.add(expr);
//            return this;
//        }
        
        public Builder withCondition(String expr)
        {
            expressions = expr;
            return this;
        }

        public Builder withConsequence(ActionDispatcher dispatcher)
        {
            String[] c = dispatcher.getClass().toString().split(" ");
            this.dispatcher = c[1];
            return this;
        }
        
        public Builder withTimeCondition(String expr)
        {
            timeExpressions.add(ExpressionParser.fromTimeString(expr));
            return this;
        }
        
        public Builder withPriority(int priority) {
            this.priority = priority;
            return this;
        }

        public Rule build()
        {
            return new Rule(expressions, timeExpressions, dispatcher, priority);
        }
    }

    private Rule(String expressions, List<String> timeExpressions, String dispatcher, Integer priority)
    {
        this.expressions = expressions;
        this.timeExpressions = timeExpressions;
        this.dispatcher = dispatcher;
        this.priority = priority;
    }
    
    public String getExpression() {
        return this.expressions;
    }
    
    public String getDispatcher() {
        return this.dispatcher;
    }
    
    public Integer getPriority() {
        return this.priority;
    }
}
