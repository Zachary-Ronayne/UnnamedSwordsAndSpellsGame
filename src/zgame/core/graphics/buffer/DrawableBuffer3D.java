package zgame.core.graphics.buffer;

import zgame.core.graphics.Destroyable;
import zgame.core.graphics.Renderer;
import zgame.core.utils.ZMath;

/**
 * A class which manages a {@link DrawableBuffer} and a plane to easily drawn as a texture in 3D.
 * By default, this will be a buffer on the xz axis facing upwards
 */
public class DrawableBuffer3D implements Destroyable{
	
	/** The buffer holding the content which will be drawn, can be null if not initialized yet */
	private DrawableBuffer buffer;
	
	/** The x center coordinate to draw the buffer */
	private double x;
	/** The y coordinate to draw the buffer */
	private double y;
	/** The z center coordinate to draw the buffer */
	private double z;
	
	/** The width to draw the buffer, i.e. size on the x axis before rotations */
	private double width;
	/** The width to draw the buffer, i.e. size on the z axis before rotations */
	private double length;
	
	/** Rotation on the x axis */
	private double rotX;
	/** Rotation on the y axis */
	private double rotY;
	/** Rotation on the z axis */
	private double rotZ;
	
	/** The opacity of this buffer, 0 for invisible, 1 for fully opaque */
	private double opacity;
	
	/** @param buffer See {@link #buffer} */
	public DrawableBuffer3D(DrawableBuffer buffer){
		this.buffer = buffer;
		this.x = 0;
		this.y = 0;
		this.z = 0;
		this.width = 1;
		this.length = 1;
		this.rotX = 0;
		this.rotY = 0;
		this.rotZ = 0;
		this.opacity = 1;
	}
	
	@Override
	public void destroy(){
		if(this.buffer != null) this.buffer.destroy();
	}
	
	/**
	 * Draw this buffer in 3D with the given renderer
	 *
	 * @param r The renderer to use
	 */
	public void render(Renderer r){
		if(this.buffer == null) return;
		
		if(!this.buffer.isBufferGenerated()) {
			this.buffer.regenerateBuffer();
			this.buffer.redraw(r);
		}
		r.pushColor();
		r.setColor(0, 0, 0, this.getOpacity());
		r.drawPlaneBuffer(
				this.getX(), this.getY(), this.getZ(),
				this.getWidth(), this.getLength(),
				// Not sure why the z rotation needs to be modified like this to display correctly, probably the weird coordinate system I'm using
				this.getRotX(), this.getRotY(), -this.getRotZ() + ZMath.PI_BY_2, 0, 0, 0,
				this.buffer.getTextureID());
		r.popColor();
	}
	
	/** @return See {@link #buffer} */
	public DrawableBuffer getBuffer(){
		return this.buffer;
	}
	
	/** @param buffer See {@link #buffer} */
	public void setBuffer(DrawableBuffer buffer){
		this.buffer = buffer;
	}
	
	/** @return See {@link #x} */
	public double getX(){
		return this.x;
	}
	
	/** @param x See {@link #x} */
	public void setX(double x){
		this.x = x;
	}
	
	/** @return See {@link #y} */
	public double getY(){
		return this.y;
	}
	
	/** @param y See {@link #y} */
	public void setY(double y){
		this.y = y;
	}
	
	/** @return See {@link #z} */
	public double getZ(){
		return this.z;
	}
	
	/** @param z See {@link #z} */
	public void setZ(double z){
		this.z = z;
	}
	
	/** @return See {@link #width} */
	public double getWidth(){
		return this.width;
	}
	
	/** @param width See {@link #width} */
	public void setWidth(double width){
		this.width = width;
	}
	
	/** @return See {@link #length} */
	public double getLength(){
		return this.length;
	}
	
	/** @param length See {@link #length} */
	public void setLength(double length){
		this.length = length;
	}
	
	/** @return See {@link #rotX} */
	public double getRotX(){
		return this.rotX;
	}
	
	/** @param rotX See {@link #rotX} */
	public void setRotX(double rotX){
		this.rotX = rotX;
	}
	
	/** @return See {@link #rotY} */
	public double getRotY(){
		return this.rotY;
	}
	
	/** @param rotY See {@link #rotY} */
	public void setRotY(double rotY){
		this.rotY = rotY;
	}
	
	/** @return See {@link #rotZ} */
	public double getRotZ(){
		return this.rotZ;
	}
	
	/** @param rotZ See {@link #rotZ} */
	public void setRotZ(double rotZ){
		this.rotZ = rotZ;
	}
	
	/** @return See {@link #opacity} */
	public double getOpacity(){
		return this.opacity;
	}
	
	/** @param opacity See {@link #opacity} */
	public void setOpacity(double opacity){
		this.opacity = opacity;
	}
}
