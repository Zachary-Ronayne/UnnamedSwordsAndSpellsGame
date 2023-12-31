package zgame.core.type;

import zgame.core.Game;
import zgame.core.graphics.Renderer;

/** The type representing a 3D game */
public class Type3D extends GameType{
	
	/** Init a new 3D game type */
	public Type3D(){}
	
	@Override
	public void setupRender(Game game, Renderer r){
		r.updateFrustum();
		r.setDepthTestEnabled(true);
		r.camera3DPerspective();
	}
	
	@Override
	public void onTypeSet(Game game){
		var buff = game.getWindow().getRenderer().getBuffer();
		buff.setDepthBufferEnabled(true);
		buff.regenerateBuffer();
	}
}
