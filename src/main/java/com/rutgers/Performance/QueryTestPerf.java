//package com.rutgers.Performance;
//
///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//
//
//import com.rutgers.Hilbert.HilbertCurve;
//import com.rutgers.DB.RocksDBMS;
//import com.rutgers.Tree.TBSTNode;
//import com.rutgers.Tree.ThreadedBinarySearchTree;
//import java.math.BigInteger;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//import net.tomp2p.peers.Number160;
//
///**
// *
// * @author eduard
// */
//public class QueryTestPerf {
//    
//    private HilbertCurve hc;
//    private List<Integer> list = new ArrayList<Integer>();
//    private ThreadedBinarySearchTree tree = null;
//    private RocksDBMS db = null;
//
//    public QueryTestPerf(int bits, int dimension) {
//        hc = HilbertCurve.bits(bits).dimensions(dimension);
//        int pow = (int) Math.pow(dimension, bits);
//        Number160 p1 = new Number160(pow);
//        Number160 p2 = new Number160(pow);
//        tree = new ThreadedBinarySearchTree(p1);
//        db = new RocksDBMS();
//    }
//    
////    public void StoreSingleData(Number160 index, String data) {
////        tree.insert(index);
////        db.putData(data, index);
////    }
////    
////    public void StoreSingleDataBatch(BigInteger[] index, String[] data) {
////        for(int i = 0; i < index.length; i++) {
////            tree.insert(index[i]);
////        }
////        db.putData(data, index);
////    }
////    
////    public byte[] ExactQuery(BigInteger index) {
////        if(tree.search(index)) {
////            return db.getData(index).get(0);
////        } else  {
////            return null;
////        }
////    }    
////    
////    public ArrayList<byte[]> ExactQueryBatch(BigInteger... indexs) {
////        ArrayList<BigInteger> list = new ArrayList<BigInteger>();
////        for(BigInteger index : indexs) {
////            if(tree.search(index)) {
////                list.add(index);
////            }
////        }
////        return db.getData(list.toArray(new BigInteger[list.size()]));
////    }
////    
////    public ArrayList<byte[]> WhildCardQuery(int wp, BigInteger index) {
////        long[] point = hc.point(index);
////        BigInteger i = new BigInteger("-1");
////        ArrayList<BigInteger> list = new ArrayList<BigInteger>();
////        TBSTNode searchNode = null;
////        searchNode = tree.searchNode(index);
////        int compareTo = searchNode.getEle().compareTo(index);
////        if(compareTo == 0) {
////            //Equal
////            list.add(index);
////        }else if(compareTo == 1){
////            //1 value bigger
////            point[wp] += 1;
////            searchNode = tree.insucc(searchNode);
////            i = hc.index(point);
////            
////            if(searchNode.getEle().compareTo(i) == 0) {
////                list.add(i);
////            }
////        } else {
////            //2 value bigger
////        }
////        
////        while(searchNode.getEle().compareTo(i) == 1) {
////            point[wp] += 1;
////            searchNode = tree.insucc(searchNode);
////            i = hc.index(point);
////            
////            if(searchNode.getEle().compareTo(i) == 0) {
////                list.add(i);
////            }
////        }
////        
////        return db.getData(list.toArray(new BigInteger[list.size()]));
////    }
//
//    
//    
//    
////    public static void main(String[] args) {
////        int iter = Integer.parseInt(args[0]);
////        int bits = 10;//Integer.parseInt(args[1]);
////        int dim = Integer.parseInt(args[1]);
////        
////        BigInteger[] indexs = new BigInteger[iter];
////        String[] strings = new String[iter];
////        QueryTestPerf q = new QueryTestPerf(bits,dim);
////        
////        for(int i = 0; i < iter; i++) {
////            BigInteger b = new BigInteger(10, new Random());
////            indexs[i] = b;
////            strings[i] = RandomStringUtils.random(5, true, false);
////        }
////        
////        long startTime = System.currentTimeMillis();
////        for(int i = 0; i < iter; i ++) {
////            q.StoreSingleData(indexs[i],strings[i]);
////        }
////        long estimatedTime = System.currentTimeMillis() - startTime;
////        System.out.println("Elapsed time insert: " + estimatedTime + " ms.");
////        
////        startTime = System.currentTimeMillis();
////        q.StoreSingleDataBatch(indexs,strings);
////        estimatedTime = System.currentTimeMillis() - startTime;
////        System.out.println("Elapsed time insert batch: " + estimatedTime + " ms.");
////        
////        for(int i = 0; i < iter; i++) {
////            BigInteger b = new BigInteger(256, new Random());
////            indexs[i] = b;
////        }
////        
////        startTime = System.currentTimeMillis();
////        for(int i = 0; i < iter; i ++) {
////            q.ExactQuery(indexs[i]);
////        }
////        estimatedTime = System.currentTimeMillis() - startTime;
////        System.out.println("Elapsed time exact query: " + estimatedTime + " ms.");
////        
////        startTime = System.currentTimeMillis();
////        q.ExactQueryBatch(indexs);
////        estimatedTime = System.currentTimeMillis() - startTime;
////        System.out.println("Elapsed time exact query batch: " + estimatedTime + " ms.");
////    }
//}
