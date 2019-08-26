package com.rutgers.QuadTree;

/**
* This class defines the AbstractQuadTree.
*
* @author  Eduard Giber Renart
* @version 1.0
*/

public abstract class AbstractQuadTree<T> {

	//protected Dimension size;
	//protected Point startCoordinates;
        protected double north, south, east, west;

	public AbstractQuadTree(double north, double south, double east, double west) {
            this.north = north;
            this.south = south;
            this.east = east;
            this.west = west;
	}

	/**
	 * Returns the north
	 * 
	 * @return
	 */
	public double getNorth() {
            return this.north;
	}
        
        public double getSouth() {
            return this.south;
        }
        
        public double getEast() {
            return this.east;
        }
        
        public double getWest() {
            return this.west;
        }

	/**
	 * Clear the QuadTree
	 */
	public abstract void clear();

	/**
	 * Return the root node of this quad tree
	 * 
	 * @return
	 */
	public abstract AbstractNode<T> getRootNode();

}
