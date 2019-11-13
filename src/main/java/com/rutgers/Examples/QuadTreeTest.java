package com.rutgers.Examples;

import static com.rutgers.Core.Globals._DEF_EAST_;
import static com.rutgers.Core.Globals._DEF_NORTH_;
import static com.rutgers.Core.Globals._DEF_SOUTH_;
import static com.rutgers.Core.Globals._DEF_WEST_;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;
import com.rutgers.QuadTree.PointQuadTree;


public class QuadTreeTest {
	
	private static PointQuadTree<String> QuadTree;
	static double latitude;
	static double longitude;
	
	public static void RandomPoint() {
		 while(!(longitude >= _DEF_WEST_ && longitude < _DEF_EAST_ && latitude <= _DEF_NORTH_ && latitude > _DEF_SOUTH_)) {
			Random r = new Random();
			double u = r.nextDouble();
			double v = r.nextDouble();
	
			latitude = Math.toDegrees(Math.acos(u*2-1)) - 90;
			longitude = 360 * v - 180;
		 }
	}
	
	public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
		
		QuadTree = new PointQuadTree<String>(_DEF_NORTH_, _DEF_SOUTH_, _DEF_EAST_, _DEF_WEST_, 4, 4);
		
		for(int i =0; i <=15000; i++) {
			QuadTree.insert(latitude, longitude, "", "");
		}
		
		RandomPoint();
		
        long start = System.nanoTime();
		QuadTree.getElements(latitude,longitude);
		long ellapsed = System.nanoTime() - start;
		System.out.println(ellapsed);
	}
}
