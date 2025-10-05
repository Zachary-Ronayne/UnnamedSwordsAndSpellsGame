package zusass.graphics;

import zgame.core.graphics.Renderer;
import zgame.core.graphics.texture.TexCoordsRectPrism3D;
import zgame.things.ThingClickDetector3D;
import zgame.things.type.bounds.ModifiableRectDims3D;
import zgame.world.Direction3D;
import zusass.ZusassGame;

/** An extension of TexCoordsRectPrism3D for abstracting rendering */
public class ZusassTexCoordsRectPrism extends TexCoordsRectPrism3D{
	
	/** See {@link super#TexCoordsRectPrism3D(String, ModifiableRectDims3D, double, double, double, Direction3D)} */
	public ZusassTexCoordsRectPrism(String name, ModifiableRectDims3D bounds, double scaleX, double scaleY, double scaleZ, Direction3D direction){
		super(name, bounds, scaleX, scaleY, scaleZ, direction);
	}
	
	/**
	 * Render this object, using a tint if it can be selected by the given
	 *
	 * @param r The rendered to use for drawing
	 * @param clickable This as a thing that can be clicked on
	 * @return true if something was rendered, false otherwise
	 */
	public boolean renderSelectable(Renderer r, ThingClickDetector3D clickable){
		var zgame = ZusassGame.get();
		var room = zgame.getCurrentRoom();
		if(room == null) return false;
		return this.renderSelectable(r, room, clickable, zgame.getPlayer());
	}
	
}
