package com.rutgers.QuadTree;

/**
 * 
 * @author eduard
 */
@SuppressWarnings("serial")
public abstract class AbstractNodeElement<T> {

	private T element;
        private double latitude, longitude;
        private String icon;

	/**
	 * Create a new NodeElement that holds the element at the given coordinates.
	 * 
	 * @param x
	 * @param y
	 * @param element
	 */
	public AbstractNodeElement(double latitude, double longitude, T element, String icon) {
		this.latitude = latitude;
                this.longitude = longitude;
		this.element = element;
                this.icon = icon;
	}

	public AbstractNodeElement(T element) {
		this.element = element;
	}

	/**
	 * Returns the element that is contained within this NodeElement
	 * 
	 * @return
	 */
	public T getElement() {
		return element;
	}
        
        public double getLongitude() {
            return longitude;
        }
        
        public double getLatitude() {
            return latitude;
        }
        
        public String getIcon() {
            return icon;
        }
}
