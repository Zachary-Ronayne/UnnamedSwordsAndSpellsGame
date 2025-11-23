package zusass.menu.savesmenu.comp;

import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.utils.ZRect2D;
import zgame.menu.scroller.MenuScrollerButton;
import zgame.menu.scroller.VerticalScroller;
import zgame.menu.scroller.VerticalScrollerButton;
import zusass.menu.comp.ZusassStyle;

/** A {@link VerticalScroller} made for the saves menu */
public class SavesMenuScroller extends VerticalScroller{
	
	/** The x coordinate of this {@link SavesMenuScroller} */
	public static final double X = 1200;
	/** The y coordinate of this {@link SavesMenuScroller} */
	public static final double Y = 20;
	
	/**
	 * Create the {@link SavesMenuScroller}
	 */
	public SavesMenuScroller(){
		super(X, Y, 25, 680, 0);
		this.setFill(new ZColor(.8));
		this.setBorder(new ZColor(0.8));
		this.setBorderWidth(2);
		
		var button = this.getButton();
		button.setFill(new ZColor(.3));
		button.setBorder(new ZColor(0.8));
		button.setBorderWidth(4);
	}
	
	@Override
	public void renderFill(Renderer r, ZRect2D bounds){
		ZusassStyle.renderGenericFill(this, r, bounds);
	}
	
	@Override
	public void renderBorderBounds(Renderer r, ZRect2D borderBounds){
		ZusassStyle.renderGenericBorderBounds(this, r, borderBounds);
	}
	
	@Override
	public MenuScrollerButton generateButton(){
		// TODO consider a way to make rendering easier to override without having to change methods entirely. Maybe make it a renderable object that can be overridden
		var button = new VerticalScrollerButton(this, this.getWidth(), this.getWidth() * 2){
			@Override
			public void renderFill(Renderer r, ZRect2D bounds){
				ZusassStyle.renderGenericFill(this, r, bounds);
			}
			
			@Override
			public void renderBorderBounds(Renderer r, ZRect2D borderBounds){
				ZusassStyle.renderGenericBorderBounds(this, r, borderBounds);
			}
		};
		button.setFullColor(new ZColor(0.4));
		return button;
	}
}
