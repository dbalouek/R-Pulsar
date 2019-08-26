package com.rutgers.QuadTree;

import com.rutgers.Core.Manager;
import com.rutgers.Core.Message;
import com.rutgers.Core.RP;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Set;
import java.util.Vector;
import net.tomp2p.peers.PeerAddress;

/**
* This is the main class of the quadtree
*
* @author  Eduard Giber Renart
* @version 1.0
*/

public class PointQuadTree<T> extends AbstractQuadTree<T> {

	protected PointNode<T> rootNode = null;
        private Manager manager = null;
        private RP rpOne, rpTwo = null;
        private ArrayList<PeerAddress> list = null;

	/**
	 * Create a new QuadTree with the give start coordinates and size
	 * 
	 * @param startCorrdinates
	 * @param size
	 */
	public PointQuadTree(double north, double south, double east, double west) throws IOException {
		super(north, south, east, west);
		this.rootNode = new PointNode<T>(north, south, east, west, 0);
                manager = Manager.getInstance();
                rpOne = manager.getRpOne();
                rpTwo = manager.getRpTwo();
                list = new ArrayList<PeerAddress>();
	}
	
	public PointQuadTree(double north, double south, double east, double west, int maxDepth, int maxChildren) {
		super(north, south, east, west);
		this.rootNode = new PointNode<T>(north, south, east, west, 0,maxDepth,maxChildren);
	}

	/**
	 * Add a new element to the QuadTree
	 * 
	 * @param point
	 * @param element
	 */
	public void insert(double latitude, double longitude, T element, String icon) throws NoSuchAlgorithmException, InvalidKeySpecException {
            
            if(!(longitude >= west && longitude < east && latitude <= north && latitude > south)) {
                throw new IndexOutOfBoundsException("The coordinate must be within bounds.");
            }
            
            this.rootNode.insert(new PointNodeElement<T>(latitude, longitude, element, icon));
            
            if(this.rootNode.subdivide) {
                System.out.println("Divided");
                this.rootNode.subdivide = false;
                Set<AbstractNode.Cell> keySet = this.rootNode.nodes.keySet();
                String elements = "";
                String directions = "";
                
                for(AbstractNode.Cell cell: keySet) {
                    
                    PeerAddress master = (PeerAddress) this.rootNode.nodes.get(cell).getElements().get(0).getElement();
                    elements = master.inetAddress().toString() + ":" + master.tcpPort();
                    
                    for(PointNodeElement<T> ele:this.rootNode.nodes.get(cell).getElements()) {
                        list.add((PeerAddress) ele.getElement());
                    }
                    
                    PointNode<T> pn = this.rootNode.nodes.get(cell);
                    directions = String.valueOf(pn.north) + "," + String.valueOf(pn.south) + "," + String.valueOf(pn.east) + "," + String.valueOf(pn.west);
                    
                    if(rpTwo != null) {
                        Message.ARMessage.Header.Profile.Builder p = Message.ARMessage.Header.Profile.newBuilder();
                        Message.ARMessage.Header h = Message.ARMessage.Header.newBuilder().setLatitude(0.00).setLongitude(0.0).setType(Message.ARMessage.RPType.AR_RP).setProfile(p).addHID(rpTwo.getId().toString()).build();
                        Message.ARMessage msgRegular = Message.ARMessage.newBuilder().setHeader(h).setAction(Message.ARMessage.Action.UPDATE).addPayload(elements).build();                        
                        Message.ARMessage msgMaster = Message.ARMessage.newBuilder().setHeader(h).setAction(Message.ARMessage.Action.UPDATE).addPayload(elements).addPayload(directions).addPayload("Master").build();  
                        
                        for (int i = 0; i < list.size(); i++) {
                            if(i == 0) {
                                rpTwo.sendDirectMessageNonBlocking(list.get(i), msgMaster);
                            }else  {
                                rpTwo.sendDirectMessageNonBlocking(list.get(i), msgRegular);
                            }
                        }

                    } else {
                        Message.ARMessage.Header.Profile.Builder p = Message.ARMessage.Header.Profile.newBuilder();
                        Message.ARMessage.Header h = Message.ARMessage.Header.newBuilder().setLatitude(0.00).setLongitude(0.0).setType(Message.ARMessage.RPType.AR_RP).setProfile(p).addHID(rpOne.getId().toString()).build();
                        Message.ARMessage msgRegular = Message.ARMessage.newBuilder().setHeader(h).setAction(Message.ARMessage.Action.UPDATE).addPayload(elements).addPayload(directions).build();
                        Message.ARMessage msgMaster = Message.ARMessage.newBuilder().setHeader(h).setAction(Message.ARMessage.Action.UPDATE).addPayload(elements).addPayload(directions).addPayload("Master").build();  
                        
                        for (int i = 0; i < list.size(); i++) {          
                            if(i == 0) {
                                rpOne.sendDirectMessageNonBlocking(list.get(i), msgMaster);
                            } else {
                                rpOne.sendDirectMessageNonBlocking(list.get(i), msgRegular);
                            }
                        }
                    }   
                    
                    list.clear();
                    elements = ""; 
                    directions = "";
                }
            }
	}

	/**
	 * Returns the rootNode of this tree
	 * 
	 * @return
	 */
	public PointNode<T> getRootNode() {
		return this.rootNode;
	}

	/**
	 * Returns all elements wihtin the cell that matches the given coordinates
	 * 
	 * @param coordinates
	 * @return
	 */
	public Vector<? extends AbstractNodeElement<T>> getElements(double latitude, double longitude) {
		return this.rootNode.getElements(latitude, longitude);
	}

	@Override
	public void clear() {
		this.rootNode.clear();
	}
}
