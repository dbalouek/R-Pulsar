package com.rutgers.QuadTree;

import java.util.Map;

/**
* This class defined the Node that will be used in the QuadTree.
*
* @author  Eduard Giber Renart
* @version 1.0
*/

public abstract class AbstractNode<T> {

	/**
	 * Default value for amount of elements
	 */
	protected final static int MAX_ELEMENTS = 4;

	/**
	 * Default value for max depth
	 */
	protected final static int MAX_DEPTH = 4;

	public static enum Cell {
		TOP_LEFT, BOTTOM_RIGHT, BOTTOM_LEFT, TOP_RIGHT
	}

        protected double north, south, east, west;
	protected int maxDepth;
	protected int maxElements;
	protected int depth;
        protected String strokeColor;
        protected String fillColor;

	public AbstractNode(double north, double south, double east, double west, int depth) {
		this(north, south, east, west, depth, MAX_ELEMENTS, MAX_DEPTH);
	}

	public AbstractNode(double north, double south, double east, double west, int depth, int maxDepth, int maxElements) {
            this.north = north;
            this.south = south;
            this.east = east;
            this.west = west;
            this.maxDepth = maxDepth;
            this.maxElements = maxElements;
            this.depth = depth;
	}

	/**
	 * Returns the North
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
	 * Returns the max elements
	 * 
	 * @return
	 */
	public int getMaxElements() {
		return this.maxElements;
	}

	/**
	 * Returns the depth of this node
	 * 
	 * @return
	 */
	public int getDepth() {
		return this.depth;
	}

	/**
	 * Returns the max depth
	 * 
	 * @return
	 */
	public int getMaxDepth() {
		return this.maxDepth;
	}
        
        public void setStrokeColor(String color) {
            strokeColor = color;
        }
        
        public void setFillColor(String color) {
            fillColor = color;
        }

	public abstract void subdivide();

	public abstract void clear();

	public abstract Map<Cell, AbstractNode<T>> getSubNodes();

}
