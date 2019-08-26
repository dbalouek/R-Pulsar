/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.RuleEngine;

/**
* This class that developers will use to implement Rules in R-Pulsar.
* 
* @author  Eduard Giber Renart
* @version 1.0
*/

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

        /**
         * Each rule consists of an expression, condition and consequence.
         * @param expr
         * @return
         */
        public Builder withExpression(Expression expr)
        {
            expressions.add(expr);
            return this;
        }
        
        /**
         * Each rule consists of an expression, condition and consequence.
         * @param expr
         * @return
         */
        public Builder withCondition(String expr)
        {
            expressions.add(ExpressionParser.fromString(expr));
            return this;
        }

        /**
         * Each rule consists of an expression, condition and consequence.
         * @param expr
         * @return
         */
        public Builder withConsequence(ActionDispatcher dispatcher)
        {
            this.dispatcher = dispatcher;
            return this;
        }
        
        /**
         * We can set a priority to a run so in the case of multiple rules being statisfied
         * only the rule with the highest priority will be used.
         * @param expr
         * @return
         */
        public Builder withPriority(int priority) {
            this.priority = priority;
            return this;
        }

        /**
         * This method is called once the rule is ready to be created.
         * @return
         */
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
    
    /**
     * Return the expression of the rule. 
     * @return
     */
    public List<Expression> getExpression() {
        return this.expressions;
    }
    
    public ActionDispatcher getDispatcher() {
        return this.dispatcher;
    }
    
    /**
     * Return the priority of the rule.
     * @return
     */
    public Integer getPriority() {
        return this.priority;
    }
}
