package zgame.core.utils;

import java.awt.geom.Rectangle2D;

/** A convenience class that just extends Rectangle2D.Double, making it easier to code with */
public class ZRect extends Rectangle2D.Double{

	public ZRect(){
		super();
	}

	public ZRect(double x, double y, double w, double h){
		super(x, y, w, h);
	}

}