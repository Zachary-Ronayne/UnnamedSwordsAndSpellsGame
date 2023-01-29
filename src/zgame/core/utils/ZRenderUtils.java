package zgame.core.utils;

import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;

/** A class used to make some basic renderings using {@link Renderer} */
@SuppressWarnings("unused")
public final class ZRenderUtils{
	
	/**
	 * Draw a checkerboard pattern of rectangles in a given rectangular space
	 *
	 * @param r The {@link Renderer} to use for the drawing operations
	 * @param x The x coordinate of the upper left hand corner
	 * @param y The y coordinate of the upper left hand corner
	 * @param w The total width of the pattern
	 * @param h The total height of the pattern
	 * @param tilesX The number of tiles on the x axis
	 * @param tilesY The number of tiles on the y axis
	 * @param c1 The first color of the tiles
	 * @param c2 The second color of the tiles
	 */
	public static void checkerboard(Renderer r, double x, double y, double w, double h, int tilesX, int tilesY, ZColor c1, ZColor c2){
		double sw = w / tilesX;
		double sh = h / tilesY;
		for(int i = 0; i < tilesX; i++){
			for(int j = 0; j < tilesY; j++){
				boolean i0 = i % 2 == 0;
				boolean j0 = j % 2 == 0;
				if(i0 == j0) r.setColor(c1);
				else r.setColor(c2);
				r.drawRectangle(x + sw * i, y + sh * j, sw, sh);
			}
		}
	}
	
	/** Cannot instantiate {@link ZRenderUtils} */
	private ZRenderUtils(){
	}
	
}
