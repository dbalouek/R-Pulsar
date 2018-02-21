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
public class NullActionDispatcher implements ActionDispatcher
{
    @Override
    public void fire()
    {
        System.out.println("Send to NULL");
    }
}
