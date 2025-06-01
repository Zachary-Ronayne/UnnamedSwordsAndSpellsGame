package zgame.core.type;

import zgame.core.Game;
import zgame.core.graphics.Renderer;

/** The type representing a 2D game */
public class Style2D implements RenderStyle{
	
	/** Init a new 2D game type */
	public Style2D(){}
	
	@Override
	public void setupFrame(Renderer r){
		// Set the camera
		var game = Game.get();
		boolean useCam = game.getCurrentState().isUseCamera();
		if(useCam) r.setCamera(game.getCamera());
		else r.setCamera(null);
		// Move based on the camera, if applicable, and draw the objects
		r.identityMatrix();
		if(useCam) r.transform(game.getCamera(), game.getWindow());
		
		r.setDepthTestEnabled(false);
	}
	
	@Override
	public void setupCore(Renderer r){}
}
