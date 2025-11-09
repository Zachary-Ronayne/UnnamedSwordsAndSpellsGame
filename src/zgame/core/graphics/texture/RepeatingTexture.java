package zgame.core.graphics.texture;

/** A class containing data used for rendering repeated textures */
public class RepeatingTexture{
	
	/** The width of the rendered texture, in pixel size */
	private double texW;
	/** The height of the rendered texture, in pixel size */
	private double texH;
	/** An amount to shift the texture on the x axis */
	private double shiftX;
	/** An amount to shift the texture on the y axis */
	private double shiftY;
	
	/**
	 * Create a new repeating texture object with no shift and a square size
	 * @param size The size for both {@link #texW} and {@link #texW}
	 */
	public RepeatingTexture(double size){
		this(size, size);
	}
	
	/**
	 * Create a new repeating texture object with no shift
	 * @param texW See{@link #texW}
	 * @param texH See{@link #texH}
	 */
	public RepeatingTexture(double texW, double texH){
		this(texW, texH, 0, 0);
	}
	
	/**
	 * Create a new repeating texture object with the given values
	 * @param texW See{@link #texW}
	 * @param texH See{@link #texH}
	 * @param shiftX See {@link #shiftX}
	 * @param shiftY See {@link #shiftY}
	 */
	public RepeatingTexture(double texW, double texH, double shiftX, double shiftY){
		this.texW = texW;
		this.texH = texH;
		this.shiftX = shiftX;
		this.shiftY = shiftY;
	}
	
	/** @return See {@link #texW} */
	public double getTexW(){
		return this.texW;
	}
	
	/** @param texW See {@link #texW} */
	public void setTexW(double texW){
		this.texW = texW;
	}
	
	/** @return See {@link #texH} */
	public double getTexH(){
		return this.texH;
	}
	
	/** @param texH See {@link #texH} */
	public void setTexH(double texH){
		this.texH = texH;
	}
	
	/** @return See {@link #shiftX} */
	public double getShiftX(){
		return this.shiftX;
	}
	
	/** @param shiftX See {@link #shiftX} */
	public void setShiftX(double shiftX){
		this.shiftX = shiftX;
	}
	
	/** @return See {@link #shiftY} */
	public double getShiftY(){
		return this.shiftY;
	}
	
	/** @param shiftY See {@link #shiftY} */
	public void setShiftY(double shiftY){
		this.shiftY = shiftY;
	}
}
