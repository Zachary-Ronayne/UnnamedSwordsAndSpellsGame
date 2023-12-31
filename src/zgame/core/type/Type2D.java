package zgame.core.type;

import zgame.core.Game;
import zgame.core.graphics.Renderer;

/** The type representing a 2D game */
public class Type2D extends GameType{
	
	/** Init a new 2D game type */
	public Type2D(){}
	
	@Override
	public void setupRender(Game game, Renderer r){
		// Set the camera
		boolean useCam = game.getCurrentState().isUseCamera();
		if(useCam) r.setCamera(game.getCamera());
		else r.setCamera(null);
		// Move based on the camera, if applicable, and draw the objects
		r.identityMatrix();
		if(useCam) game.getCamera().transform(game.getWindow());
	}
	
	@Override
	public void onTypeSet(Game game){}
}
