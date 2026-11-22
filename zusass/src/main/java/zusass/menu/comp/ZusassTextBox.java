package zusass.menu.comp;

import zgame.core.graphics.Renderer;
import zgame.core.utils.ZRect2D;
import zgame.menu.MenuTextBox;
import zusass.ZusassGame;

/** A {@link MenuTextBox} used by the Zusass game */
public class ZusassTextBox extends MenuTextBox{
	
	/**
	 * Create a new {@link ZusassTextBox} with the given values
	 * 
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 */
	public ZusassTextBox(double x, double y, double w, double h){
		super(x, y, w, h);
		ZusassStyle.applyStyleText(this);
		this.bufferWidthToWindow(ZusassGame.window());
	}
	
	@Override
	public void renderFill(Renderer r, ZRect2D bounds){
		ZusassStyle.renderGenericFill(this, r, bounds);
	}
	
	@Override
	public void renderBorderBounds(Renderer r, ZRect2D borderBounds){
		ZusassStyle.renderGenericBorderBounds(this, r, borderBounds);
	}
	
}
