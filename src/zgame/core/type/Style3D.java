package zgame.core.type;

import zgame.core.Game;
import zgame.core.graphics.Renderer;

/** The type representing a 3D game */
public class Style3D implements RenderStyle{
	
	/** Init a new 3D game type */
	public Style3D(){}
	
	@Override
	public void setupFrame(Game game, Renderer r){
		r.updateFrustum(game.getCamera3D());
		
		r.setDepthTestEnabled(true);
		game.camera3DPerspective();
	}
	
	@Override
	public void setupCore(Game game, Renderer r){
		var window = game.getWindow();
		
		// Turn on the depth buffer
		var buff = r.getBuffer();
		buff.setDepthBufferEnabled(true);
		buff.regenerateBuffer();
		
		// Auto resize the window
		window.setResizeScreenOnResizeWindow(true);
		
		// Update the mouse mode
		window.updateMouseNormally();
	}
	
	@Override
	public void onMenuOpened(Game game){
		RenderStyle.super.onMenuOpened(game);
		
		game.getWindow().setMouseNormally(true);
	}
	
	@Override
	public void onAllMenusClosed(Game game){
		RenderStyle.super.onAllMenusClosed(game);
		
		game.getWindow().setMouseNormally(false);
	}
	
	@Override
	public void mouseMove(Game game, double x, double y){
		RenderStyle.super.mouseMove(game, x, y);
		
		var window = game.getWindow();
		if(!window.isMouseNormally()) game.getCamera3D().look(game, x, y);
	}
}
