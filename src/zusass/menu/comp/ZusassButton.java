package zusass.menu.comp;

import zgame.core.graphics.Renderer;
import zgame.core.graphics.image.ImageManager;
import zgame.core.graphics.texture.RepeatingTexture;
import zgame.core.utils.ZRect2D;
import zgame.menu.MenuButton;

/** A {@link MenuButton} which is used by the ZusassGame */
public abstract class ZusassButton extends MenuButton{
	
	/** A generic object holding how buttons should generally draw their textures */
	private final static RepeatingTexture BUTTON_TEXTURE = new RepeatingTexture(128);
	
	/** true to prevent the click sound from being played for this button, false otherwise */
	private boolean disableClickSound;
	
	/**
	 * Create a {@link ZusassButton} with the appropriate parameters
	 *
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 * @param text The text to display
	 */
	public ZusassButton(double x, double y, double w, double h, String text){
		super(x, y, w, h, text);
		
		// issue#30 why does using a buffer here make some buttons not display?
//		this.setDefaultUseBuffer(true);
		
		ZusassStyle.applyStyleText(this);
		this.setFontSize(40);
		this.centerText();
	}
	
	@Override
	public void renderFill(Renderer r, ZRect2D bounds){
		r.setColor(this.getFill());
		r.pushTextureTintShader();
		r.drawRepeatingTexture(bounds, BUTTON_TEXTURE, ImageManager.image("smoothStoneGrayscale"));
		r.popShader();
		r.popColor();
	}

	@Override
	public void renderBorderBounds(Renderer r, ZRect2D borderBounds){
		r.pushTextureTintShader();
		r.drawRepeatingTexture(borderBounds, BUTTON_TEXTURE, ImageManager.image("smoothStoneGrayscale"));
		r.popShader();
	}
	
	@Override
	public void click(){
		super.click();
		if(!this.isDisableClickSound()) ZusassStyle.playClickSound();
	}
	
	/** @return See {@link #disableClickSound} */
	public boolean isDisableClickSound(){
		return this.disableClickSound;
	}
	
	/** @param disableClickSound See {@link #disableClickSound} */
	public void setDisableClickSound(boolean disableClickSound){
		this.disableClickSound = disableClickSound;
	}
}