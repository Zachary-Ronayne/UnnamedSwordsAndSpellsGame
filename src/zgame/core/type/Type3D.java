package zgame.core.type;

import zgame.core.Game;
import zgame.core.graphics.Renderer;

/** The type representing a 3D game */
public class Type3D implements GameType{
	
	/** Init a new 3D game type */
	public Type3D(){}
	
	@Override
	public void setupRender(Game game, Renderer r){
		r.updateFrustum(-5.0, 5.0, -5.0, 5.0, 0.01, 1000.0);
		
		r.setDepthTestEnabled(true);
		r.camera3DPerspective();
	}
	
	@Override
	public void onTypeSet(Game game){
		var window = game.getWindow();
		
		// Turn on the depth buffer
		var buff = window.getRenderer().getBuffer();
		buff.setDepthBufferEnabled(true);
		buff.regenerateBuffer();
		
		// Auto resize the window
		window.setResizeScreenOnResizeWindow(true);
		
		// Update the mouse mode
		window.updateMouseNormally(window.isMouseNormally());
		
		// TODO fix the resolution not auto updating when going to full screen
		
		// TODO fix the depth buffer not updating
	}
}
