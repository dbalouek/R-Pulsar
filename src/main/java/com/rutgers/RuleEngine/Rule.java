/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.RuleEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Rule
{
    public List<Expression> expressions;
    public ActionDispatcher dispatcher;
    public Integer priority = 0;

    public static class Builder
    {
        private List<Expression> expressions = new ArrayList<>();
        private ActionDispatcher dispatcher = new NullActionDispatcher();
        private Integer priority;

        public Builder withExpression(Expression expr)
        {
            expressions.add(expr);
            return this;
        }
        
        public Builder withCondition(String expr)
        {
            expressions.add(ExpressionParser.fromString(expr));
            return this;
        }

        public Builder withConsequence(ActionDispatcher dispatcher)
        {
            this.dispatcher = dispatcher;
            return this;
        }
        
        public Builder withPriority(int priority) {
            this.priority = priority;
            return this;
        }

        public Rule build()
        {
            return new Rule(expressions, dispatcher, priority);
        }
    }

    private Rule(List<Expression> expressions, ActionDispatcher dispatcher, Integer priority)
    {
        this.expressions = expressions;
        this.dispatcher = dispatcher;
        this.priority = priority;
    }
    
    public List<Expression> getExpression() {
        return this.expressions;
    }
    
    public ActionDispatcher getDispatcher() {
        return this.dispatcher;
    }
    
    public Integer getPriority() {
        return this.priority;
    }
}
