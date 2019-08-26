/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.RuleEngine;

/**
* This class is to evaluate the rules that have been created.
* 
* @author  Eduard Giber Renart
* @version 1.0
*/

import java.util.ArrayList;
import java.util.List;

public class RuleParser 
{
    public List<Expression> expressions;
    public List<String> timeExpressions;
    public ActionDispatcher dispatcher;
    public Integer priority;

    public static class Builder
    {
        private List<String> timeExpressions = new ArrayList<>();
        private List<Expression> expressions = new ArrayList<>();
        private ActionDispatcher dispatcher = new NullActionDispatcher();
        private Integer priority;
        
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
        
        public Builder withTimeCondition(String expr)
        {
            timeExpressions.add(ExpressionParser.fromTimeString(expr));
            return this;
        }
        
        public Builder withPriority(int priority) {
            this.priority = priority;
            return this;
        }

        public RuleParser build()
        {
            return new RuleParser(expressions, timeExpressions, dispatcher, priority);
        }
    }

    private RuleParser(List<Expression> expressions, List<String> timeExpressions, ActionDispatcher dispatcher, Integer priority)
    {
        this.expressions = expressions;
        this.timeExpressions = timeExpressions;
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
