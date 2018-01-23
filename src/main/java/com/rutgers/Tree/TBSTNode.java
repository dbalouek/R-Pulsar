/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.Tree;

import net.tomp2p.peers.Number640;

/**
 *
 * @author eduard
 */
public class TBSTNode
{
    Number640 ele;
    TBSTNode left, right;
    boolean leftThread, rightThread;
 
    /** Constructor **/
    public TBSTNode(Number640 ele, boolean leftThread, boolean rightThread)
    {
        this(ele, null, null, true, true);
    }
 
    /** Constructor **/
    public TBSTNode(boolean leftThread, boolean rightThread)
    {
        this.left = this;
        this.right = this;
        this.leftThread = leftThread;
        this.rightThread = rightThread;
    }
 
    /** Constructor **/
    public TBSTNode(Number640 ele, TBSTNode left, TBSTNode right, boolean leftThread, boolean rightThread)
    {
        this.ele = ele;
        this.left = left;
        this.right = right;
        this.leftThread = leftThread;
        this.rightThread = rightThread;
    }
    
    public Number640 getEle() {
        return ele;
    }
}
