package com.rutgers.QuadTree;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 
 * @author eduard
 */
public class PointNode<T> extends AbstractNode {

	protected static Logger log = Logger.getLogger(PointNode.class);

	protected Map<Cell, PointNode<T>> nodes = new HashMap<Cell, PointNode<T>>();
        
        protected Map<Cell, PointNode<T>> testNodes = new HashMap<Cell, PointNode<T>>();

        protected Map<Cell, Integer> count = new HashMap<Cell, Integer>();
        
        protected boolean subdivide = false;

	/**
	 * Holds all elements for this node
	 */
	protected Vector<PointNodeElement<T>> elements = new Vector<PointNodeElement<T>>();

	public PointNode(double north, double south, double east, double west, int depth) {
            super(north, south, east, west, depth);
            log.debug("Creating new Node at depth " + depth);
	}

	/**
	 * 
	 * @param startCoordinates
	 * @param bounds
	 * @param depth
	 * @param maxDepth
	 * @param maxChildren
	 */
	public PointNode(double north, double south, double east, double west, int depth,int maxDepth, int maxChildren) {
            super(north, south, east, west, depth, maxDepth, maxChildren);
            log.debug("Creating new Node at depth " + depth);
	}

	/**
	 * Returns the subnodes of this node
	 * 
	 * @return
	 */
	public Map<Cell, PointNode<T>> getSubNodes() {
		return this.nodes;
	}

	/**
	 * Returns the cell of this element
	 * 
	 * @param element
	 * @return
	 */
	protected Cell findIndex(double latitude, double longitude) {
            // Compute the sector for the coordinates
            boolean left = (latitude > ((this.north + this.south) / 2)) ? false: true;
            boolean top = (longitude > ((this.east + this.west) / 2)) ? false: true;

            // top left
            Cell index = Cell.BOTTOM_LEFT;
            if (left) {
                    // left side
                    if (!top) {
                            // bottom left
                            index = Cell.TOP_LEFT;
                    }
            } else {
                    // right side
                    if (top) {
                            // top right
                            index = Cell.BOTTOM_RIGHT;
                    } else {
                            // bottom right
                            index = Cell.TOP_RIGHT;

                    }
            }
            log.debug("Coordinate [" + latitude + "-" + longitude + "] is within " + index.toString() + " at depth " + depth);
            return index;
	}

	/**
	 * Returns all elements for this node
	 * 
	 * @return
	 */
	public Vector<PointNodeElement<T>> getElements() {
            return this.elements;
	}

	/**
	 * Returns all elements within the cell that matches the given coordinates
	 * 
	 * @param coordinates
	 * @return
	 */
	public Vector<PointNodeElement<T>> getElements(double latitude, double longitude) {
            // Check if this node has already been subdivided. Therefor this node
            // should contain no elements
            if (nodes.size() > 0) {
                    Cell index = findIndex(latitude, longitude);
                    PointNode<T> node = this.nodes.get(index);
                    return node.getElements(latitude, longitude);
            } else {
                    return this.elements;
            }
	}

	/**
	 * Insert the element into this node. If needed a subdivision will be
	 * performed
	 * 
	 * @param element
	 */
	public void insert(PointNodeElement<T> element) {
            System.out.println("Inserting element into Node at depth " + depth);
            // If this Node has already been subdivided just add the elements to the
            // appropriate cell
            if (this.nodes.size() != 0) {
                    Cell index = findIndex(element.getLatitude(), element.getLongitude());
                    System.out.println("Inserting into existing cell: " + index);
                    this.nodes.get(index).insert(element);
                    return;
            }

            // Add the element to this node
            this.elements.add(element);

            System.out.println("Depth: " + depth + " " + maxDepth + " Elements: " + this.elements.size() + " " + maxElements);

            
            // Only subdivide the node if it contain more than MAX_CHILDREN and is
            // not the deepest node            
            if (!(this.depth >= maxDepth) && this.elements.size() > maxElements && this.testSubdivide()) {
                    this.subdivide();

                    // Recall insert for each element. This will move all elements of
                    // this node into the new nodes at the appropriate cell
                    for (PointNodeElement<T> current : elements) {
                            this.insert(current);
                    }
                    // Remove all elements from this node since they were moved into
                    // subnodes
                    this.elements.clear();

            }
	}

	/**
	 * Subdivide the current node and add subnodes
	 */
	public void subdivide() {
            subdivide = true;
            System.out.println("Subdividing node at depth " + depth);
            int depth = this.depth + 1;
            
            double bNorth = this.north;
            double bSouth = this.south;
            
            double boundsNE = (this.north + this.south) / 2;
            double boundsSW = (this.east + this.west) / 2;
            
            System.out.println("bNorth: " + bNorth);
            System.out.println("bSouth: " + bSouth);
            System.out.println("boundsNE: " + boundsNE);
            System.out.println("boundsSW: " + boundsSW);


            PointNode<T> cellNode = null;

            // top left
            System.out.println("BLeft: " + boundsNE + "," +  bSouth + "," + boundsSW + "," + this.west);
            cellNode = new PointNode<T>(boundsNE, bSouth, boundsSW, this.west, depth, this.maxDepth, this.maxElements);
            this.nodes.put(Cell.BOTTOM_LEFT, cellNode);

            // top right
            System.out.println("BRight: " + boundsNE + "," +  bSouth + "," + this.east + "," + boundsSW);
            cellNode = new PointNode<T>(boundsNE, bSouth, this.east, boundsSW, depth, this.maxDepth, this.maxElements);
            this.nodes.put(Cell.BOTTOM_RIGHT, cellNode);

            // bottom left
            System.out.println("TLeft: " + bNorth + "," +  boundsNE + "," + boundsSW + "," + this.west);
            cellNode = new PointNode<T>(bNorth, boundsNE, boundsSW, this.west, depth, this.maxDepth, this.maxElements);
            this.nodes.put(Cell.TOP_LEFT, cellNode);

            // bottom right
            System.out.println("TRight: " + bNorth + "," +  boundsNE + "," + this.east + "," + boundsSW);
            cellNode = new PointNode<T>(bNorth, boundsNE, this.east, boundsSW, depth, this.maxDepth, this.maxElements);
            this.nodes.put(Cell.TOP_RIGHT, cellNode);
	}
        
        /**
	 * Test if we can subdivide the current node and add subnodes
	 */
        public boolean testSubdivide() {
            System.out.println("Subdividing node at depth " + depth);
            int depth = this.depth + 1;
            
            double bNorth = this.north;
            double bSouth = this.south;
            
            double boundsNE = (this.north + this.south) / 2;
            double boundsSW = (this.east + this.west) / 2;
            
            System.out.println("bNorth: " + bNorth);
            System.out.println("bSouth: " + bSouth);
            System.out.println("boundsNE: " + boundsNE);
            System.out.println("boundsSW: " + boundsSW);

            PointNode<T> cellNode = null;

            // top left
            System.out.println("BLeft: " + boundsNE + "," +  bSouth + "," + boundsSW + "," + this.west);
            cellNode = new PointNode<T>(boundsNE, bSouth, boundsSW, this.west, depth, this.maxDepth, this.maxElements);
            this.testNodes.put(Cell.BOTTOM_LEFT, cellNode);

            // top right
            System.out.println("BRight: " + boundsNE + "," +  bSouth + "," + this.east + "," + boundsSW);
            cellNode = new PointNode<T>(boundsNE, bSouth, this.east, boundsSW, depth, this.maxDepth, this.maxElements);
            this.testNodes.put(Cell.BOTTOM_RIGHT, cellNode);

            // bottom left
            System.out.println("TLeft: " + bNorth + "," +  boundsNE + "," + boundsSW + "," + this.west);
            cellNode = new PointNode<T>(bNorth, boundsNE, boundsSW, this.west, depth, this.maxDepth, this.maxElements);
            this.testNodes.put(Cell.TOP_LEFT, cellNode);

            // bottom right
            System.out.println("TRight: " + bNorth + "," +  boundsNE + "," + this.east + "," + boundsSW);
            cellNode = new PointNode<T>(bNorth, boundsNE, this.east, boundsSW, depth, this.maxDepth, this.maxElements);
            this.testNodes.put(Cell.TOP_RIGHT, cellNode);
            
            for (PointNodeElement<T> current : elements) {
                Cell index = findIndex(current.getLatitude(), current.getLongitude());
                Integer get = count.get(index);
                
                if(get == null)
                    get = 0;
                
                count.put(index, get + 1);
            }
            
            if(count.get(Cell.TOP_LEFT) != null && count.get(Cell.TOP_RIGHT) != null && count.get(Cell.BOTTOM_LEFT) != null && count.get(Cell.BOTTOM_RIGHT) != null)
                return count.get(Cell.TOP_LEFT) >= 2 && count.get(Cell.TOP_RIGHT) >= 2 && count.get(Cell.BOTTOM_LEFT) >= 2 && count.get(Cell.BOTTOM_RIGHT) >= 2;
            else 
                return false;
        }

	/**
	 * Clears this node and all subnodes
	 */
	public void clear() {
            for (PointNode<T> node : nodes.values()) {
                    node.clear();
            }
            elements.clear();
            nodes.clear();
	}
}
