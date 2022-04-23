package zgame.core.graphics;

/** An object which keeps track of the red, green, blue, and alpha (transparency) values of a color */
public class ZColor{
	
	/** The red component, in the range [0-1] */
	private double r;
	/** The blue component, in the range [0-1] */
	private double g;
	/** The green component, in the range [0-1] */
	private double b;
	/** The alpha (transparency) component, in the range [0-1] */
	private double a;
	
	/**
	 * Create a gray color with no transparency
	 * 
	 * @param g The value for all 3 channels, red, green, and blue
	 */
	public ZColor(double g){
		this(g, g, g, 1);
	}

	/**
	 * Create a gray color with transparency
	 * 
	 * @param g The value for all 3 channels, red, green, and blue
	 * @param a See {@link #a}
	 */
	public ZColor(double g, double a){
		this(g, g, g, a);
	}

	/**
	 * Create a new color with no transparency
	 * 
	 * @param r See {@link #r}
	 * @param g See {@link #g}
	 * @param b See {@link #b}
	 */
	public ZColor(double r, double g, double b){
		this(r, g, b, 1);
	}
	
	/**
	 * Create a new color
	 * 
	 * @param r See {@link #r}
	 * @param g See {@link #g}
	 * @param b See {@link #b}
	 * @param a See {@link #a}
	 */
	public ZColor(double r, double g, double b, double a){
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	/** See {@link #g} */
	public double red(){
		return this.r;
	}
	
	/** See {@link #g} */
	public double green(){
		return this.g;
	}
	
	/** See {@link #b} */
	public double blue(){
		return this.b;
	}
	
	/** See {@link #a} */
	public double alpha(){
		return this.a;
	}

	/** @return a copy of this {@link ZColor} but with an alpha value of 1, i.e. fully visible */
	public ZColor solid(){
		return new ZColor(this.red(), this.green(), this.blue(), 1);
	}

	/** @return An array of 4 elements containing the red, green, blue, and alpha values of this color, index 0, 1, 2, 3 respectively, as floats */
	public float[] toFloat(){
		return new float[]{(float)this.red(), (float)this.green(), (float)this.blue(), (float)this.alpha()};
	}
	
}
