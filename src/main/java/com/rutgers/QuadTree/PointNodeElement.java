package com.rutgers.QuadTree;

/**
* This class defines the Point Node Element.
*
* @author  Eduard Giber Renart
* @version 1.0
*/

@SuppressWarnings("serial")
public class PointNodeElement<T> extends AbstractNodeElement<T> {

	public PointNodeElement(double latitude, double longitude, T element, String icon) {
		super(latitude, longitude, element, icon);
	}

}
