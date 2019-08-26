///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.rutgers.QuadTree;
//
///**
// *
// * @author eduard
// */
//public class ColorRamp {
//    
//    public static class Color {
//        private static double r,g,b;
//        
//        public Color(double r, double g, double b) {
//            this.r = r;
//            this.g = g;
//            this.b = b;
//        }
//        
//        public double getRed() {
//            return r;
//        }
//        
//        public double getGreen() {
//            return g;
//        }
//        
//        public double getBlue() {
//            return b;
//        }
//    }
//    
//    public static String GetColor(double latency, double minLatency, double maxLatency) {
//        Color c = new Color(1.0,1.0,1.0);
//        double dv;
//        
//        if (latency < minLatency)
//            latency = minLatency;
//        
//        if (latency > maxLatency)
//            latency = maxLatency;
//            
//        dv = maxLatency - minLatency;
//
//        if (latency < (minLatency + 0.25 * dv)) {
//            c.r = 0;
//            c.g = 4 * (latency - minLatency) / dv;
//        } else if (latency < (minLatency + 0.5 * dv)) {
//            c.r = 0;
//            c.b = 1 + 4 * (minLatency + 0.25 * dv - latency) / dv;
//        } else if (latency < (minLatency + 0.75 * dv)) {
//            c.r = 4 * (latency - minLatency - 0.5 * dv) / dv;
//            c.b = 0;
//        } else {
//            c.g = 1 + 4 * (minLatency + 0.75 * dv - latency) / dv;
//            c.b = 0;
//        }
//        
//        return String.format("#%02x%02x%02x", c.r, c.g, c.b);
//    }
//}
