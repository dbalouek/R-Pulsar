/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.Core;

import net.tomp2p.peers.Number160;

/**
* This class is used to implement the wildcard in the R-Pulsar profiles.
* 
* @author  Eduard Giber Renart
* @version 1.0
*/
public class Wildcard {
    
    final int MAX = 10001;
    
    Wildcard() {
        
    }
     
    // updating the bit array of a valid block
    void update(Number160 idx, int blk, int val, int bit[][])
    {
        int id = idx.intValue();
        for (; id<MAX; id += (id&-id))
            bit[blk][id] += val;
    }
      
    // answering the query
    int query(int l, int r, Number160 k, Number160 arr[], int blk_sz, int bit[][])
    {
        // traversing the first block in range
        int sum = 0;
        while (l<r && l%blk_sz!=0 && l!=0)
        {
            if (arr[l].compareTo(k) == -1 || arr[l].compareTo(k) == 0)
                sum++;
            l++;
        }
      
        // Traversing completely overlapped blocks in
        // range for such blocks bit array of that block
        // is queried
        while (l + blk_sz <= r)
        {
            int idx = k.intValue();
            for (; idx > 0 ; idx -= idx&-idx)
                sum += bit[l/blk_sz][idx];
            l += blk_sz;
        }
      
        // Traversing the last block
        while (l <= r)
        {
            if (arr[l].compareTo(k) == -1 || arr[l].compareTo(k) == 0)
                sum++;
            l++;
        }
        return sum;
    }
      
    // Preprocessing the array
    void preprocess(Number160 arr[], int blk_sz, int n, int bit[][])
    {
        for (int i=0; i<n; i++)
            update(arr[i], i/blk_sz, 1, bit);
    }
    
    void preprocessUpdate(int i, Number160 v, int blk_sz, Number160 arr[], int bit[][])
    {
        // updating the bit array at the original
        // and new value of array
        update(arr[i], i/blk_sz, -1, bit);
        update(v, i/blk_sz, 1, bit);
        arr[i] = v;
    }
}
