/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.Tree;

import net.tomp2p.peers.Number640;

/**
* This implements a simple binary search tree.
* This is a requirement from the DHT implementation of TomP2P.
* 
* @author  Eduard Giber Renart
* @version 1.0
*/

public class ThreadedBinarySearchTree
{
    private TBSTNode root;
 
    /** Constructor **/
    public ThreadedBinarySearchTree(Number640 max) 
    {
        root = new TBSTNode(max, true, false);      
    }
 
    /** Function to clear tree **/
    public void clear()
    {
        root = new TBSTNode(true, false);  
    }
 
    /** Function to insert an element **/
    public void insert(Number640 ele) 
    {
        TBSTNode ptr = findNode(root, ele);
        
        /** element already present **/
        if (ptr == null)
            return;    
        
        int res;
        res = ptr.ele.compareTo(ele);
 
        if (res == -1) 
        { 
            TBSTNode nptr = new TBSTNode(ele, ptr, ptr.right, true, true);            
            ptr.right = nptr;
            ptr.rightThread = false;
        }
        else
        {
            TBSTNode nptr = new TBSTNode(ele, ptr.left, ptr, true, true);         
            ptr.left = nptr;
            ptr.leftThread = false;
        }
    }
 
    /** function to find node **/
    public TBSTNode findNode(TBSTNode r, Number640 ele)
    {
        int res;
        res = r.ele.compareTo(ele);
        
        if (res == -1)
        {
            if (r.rightThread)
                return r;
            return findNode(r.right, ele);
        }
        else if (res == 1)
        {
            if (r.leftThread)
                return r;
            return findNode(r.left, ele);
        }
        else
            return null;        
    }
    
    public TBSTNode findNode2(TBSTNode r, Number640 ele)
    {
        int res;
        res = r.ele.compareTo(ele);
        
        if (res == -1)
        {
            if (r.rightThread)
                return r;
            return findNode2(r.right, ele);
        }
        else if (res == 1)
        {
            if (r.leftThread)
                return r;
            return findNode2(r.left, ele);
        }
        else
            return r;        
    }
 
    /** Function to search for an element **/
    public boolean search(Number640 ele) 
    {
        return findNode(root, ele) == null;
    }
    
    public TBSTNode searchNode(Number640 ele) 
    {
        return findNode2(root, ele);
    }
 
    /** Function to print tree **/
    public void inOrder() 
    {
        TBSTNode temp = root;
        for (;;)
        {
            temp = insucc(temp);
            if (temp == root)
                break;
            System.out.print(temp.ele +" ");
        }
    } 
 
    /** Function to get inorder successor **/
    public TBSTNode insucc(TBSTNode tree)
    {
        TBSTNode temp;
        temp = tree.right;
        if (!tree.rightThread)
            while (!temp.leftThread)
                temp = temp.left;
        return temp;
    }
    
    /** Function to get inorder predecessor **/
    public TBSTNode inpred(TBSTNode tree)
    {
        TBSTNode temp;
        temp = tree.left;
        if (!tree.leftThread)
            while (!temp.rightThread)
                temp = temp.right;
        return temp;
    }
}
