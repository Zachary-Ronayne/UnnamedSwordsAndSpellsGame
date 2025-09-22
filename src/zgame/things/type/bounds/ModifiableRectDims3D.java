package zgame.things.type.bounds;

import zgame.core.utils.ZRect3D;

/** An object with methods that allow for setting the dimensions of an object, as well as obtaining the current dimentiosn */
public interface ModifiableRectDims3D{
	
	/** @param width The new width to use */
	void setWidth(double width);
	/** @param height The new height to use */
	void setHeight(double height);
	/** @param length The new length to use */
	void setLength(double length);
	
	/** @return The current full bounds of this object */
	ZRect3D getBounds();
	
}
