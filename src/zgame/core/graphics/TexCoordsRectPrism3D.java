package zgame.core.graphics;

import zgame.core.graphics.buffer.VertexBuffer;

/** An object holding texture coordinates for a rectangular prism */
public class TexCoordsRectPrism3D{
	
	/**
	 * The vertices for how to draw the rectangular prism. Should be of length 6 * 4 * 2,
	 * i.e. 6 faces, 4 vertices per face, 2 coordinates per vertex
	 */
	private final float[] data;
	
	/**
	 * Create a new object representing texture coordinates for a rectangular prism.
	 *
	 * @param data The vertices which will be flattened to a 1D array. Indexed as data[face][vertex][coordinate],
	 * 		<br/>
	 * 		face, length 6, is indexed as, [0-5], [front, back, left, right, top, bottom],
	 * 		Where with no rotations, this object is facing north, and the front face is the north most face of the object,
	 * 		meaning if an observer is also facing north and can see the object, i.e. the observer is behind the object, then
	 * 		the observer will see the back face of the object
	 * 		<br/>
	 * 		vertex, length 4, is indexed as {@link Renderer}'s standard texture coordinates, bottom left, bottom right, upper right, upper left
	 * 		<br/>
	 * 		coordinate, length 2, is indexed as x then y
	 */
	public TexCoordsRectPrism3D(float[][][] data){
		this.data = new float[6 * 4 * 2];
		int i = 0;
		for(int f = 0; f < 6; f++){
			for(int v = 0; v < 4; v++){
				for(int c = 0; c < 2; c++){
					this.data[i] = data[f][v][c];
					i++;
				}
			}
		}
	}
	
	/** @return See {@link #data} */
	public float[] getData(){
		return this.data;
	}
	
	/**
	 * Update the given buffer so that the data of this object is assigned to the buffer
	 *
	 * @param buffer The buffer to update the data to
	 */
	public void updateVertexBuffer(VertexBuffer buffer){
		buffer.updateData(this.getData());
	}
	
}
