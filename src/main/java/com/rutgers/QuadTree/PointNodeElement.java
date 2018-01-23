package com.rutgers.QuadTree;

@SuppressWarnings("serial")
public class PointNodeElement<T> extends AbstractNodeElement<T> {

	public PointNodeElement(double latitude, double longitude, T element, String icon) {
		super(latitude, longitude, element, icon);
	}

}
