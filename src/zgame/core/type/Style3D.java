package zgame.core.type;

import zgame.core.Game;
import zgame.core.graphics.Renderer;

/** The type representing a 3D game */
public class Style3D implements RenderStyle{
	
	/** Init a new 3D game type */
	public Style3D(){}
	
	@Override
	public void setupFrame(Renderer r){
		var game = Game.get();
		r.updateFrustum(game.getCamera3D());
		
		r.setDepthTestEnabled(true);
		game.camera3DPerspective();
	}
	
	@Override
	public void setupCore(Renderer r){
		var window = Game.get().getWindow();
		
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
	public void onMenuOpened(){
		RenderStyle.super.onMenuOpened();
		
		Game.get().getWindow().setMouseNormally(true);
	}
	
	@Override
	public void onAllMenusClosed(){
		RenderStyle.super.onAllMenusClosed();
		
		Game.get().getWindow().setMouseNormally(false);
	}
	
	@Override
	public void mouseMove(double x, double y){
		RenderStyle.super.mouseMove(x, y);
		
		var game = Game.get();
		var window = game.getWindow();
		if(!window.isMouseNormally()) game.look3D();
	}
}
