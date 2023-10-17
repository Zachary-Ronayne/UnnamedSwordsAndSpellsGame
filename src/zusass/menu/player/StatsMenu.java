package zusass.menu.player;

import org.lwjgl.glfw.GLFW;
import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.graphics.buffer.DrawableBuffer;
import zgame.core.utils.ZRect;
import zgame.menu.MenuThing;
import zgame.menu.format.MenuFormatter;
import zgame.menu.format.MultiFormatter;
import zgame.menu.format.PercentFormatter;
import zgame.menu.format.PixelFormatter;
import zusass.ZusassGame;

/** The menu which displays on top of the game */
public class StatsMenu extends DraggableMenu{
	
	/** The position, relative to this menu, for the maximum height of the scroller */
	public static final double SCROLLER_POSITION = DraggableMenu.DRAGGABLE_HEIGHT + DraggableMenu.BORDER_SIZE * 2;
	/** The number of seconds to wait between stat updates */
	public static final double STAT_UPDATE_TIME = 0.5;
	
	/** The list displaying the stats */
	private StatList statList;
	
	/** The amount of time, in seconds, since the last time stats were updated */
	private double lastStatUpdate;
	
	/** true to display decimal places on stats, false otherwise */
	private boolean displayDecimals;
	
	/** true if shift was held down, false otherwise */
	private boolean shiftDown;
	
	/** The buffer used to draw the popup showing a stat description */
	private StatPopup statPopup;
	
	/** The last stat list item which was popped up, or null if one wasn't popped up */
	private StatListItem lastPopupItem;
	
	/**
	 * Create a new {@link StatsMenu} for displaying the spells of something
	 *
	 * @param zgame The game which will use this menu
	 */
	public StatsMenu(ZusassGame zgame){
		super(zgame);
		this.lastStatUpdate = 0;
		this.setWidth(350);
		this.initMenuThings(zgame);
		this.displayDecimals = false;
		this.statPopup = null;
		this.lastPopupItem = null;
	}
	
	@Override
	public void destroy(){
		super.destroy();
		if(this.statPopup != null) this.statPopup.destroy();
	}
	
	@Override
	public MenuThing getScrollableMovingThing(ZusassGame zgame){
		this.statList = new StatList(this, zgame, this.getMob());
		return this.statList;
	}
	
	@Override
	public MenuFormatter getDefaultFormatter(){
		return new MultiFormatter(new PixelFormatter(10.0,  null, null, null), new PercentFormatter(null, 0.8, null, 0.5));
	}
	
	@Override
	public double getScrollWheelStrength(){
		return SpellListButton.HEIGHT + SpellListButton.HEIGHT_SPACE;
	}
	
	@Override
	public double getFullScrollableSize(){
		return this.statList.getHeight();
	}
	
	@Override
	public void regenerateThings(ZusassGame zgame){
		this.statList.regenerateText(zgame, this.getMob());
		this.lastStatUpdate = 0;
	}
	
	@Override
	public void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.keyAction(game, button, press, shift, alt, ctrl);
		if(button != GLFW.GLFW_KEY_LEFT_SHIFT && button != GLFW.GLFW_KEY_RIGHT_SHIFT) return;
		
		if(press == this.shiftDown) return;
		this.shiftDown = press;
		this.displayDecimals = press;
		this.regenerateThings((ZusassGame)game);
	}
	
	@Override
	public void tick(Game game, double dt){
		super.tick(game, dt);
		if(this.lastStatUpdate >= STAT_UPDATE_TIME) this.regenerateThings((ZusassGame)game);
		else this.lastStatUpdate += dt;
	}
	
	@Override
	public void renderOnTop(Game game, Renderer r, ZRect bounds){
		super.renderOnTop(game, r, bounds);
		
		var currentSelected = this.statList.getSelectedStat();
		// If the last drawn popup is not the currently selected one, regenerate the buffer
		if(this.lastPopupItem != currentSelected){
			// Only make a new popup if there is a selected item
			if(currentSelected != null) {
				if(this.statPopup != null) this.statPopup.destroy();
				this.statPopup = new StatPopup(currentSelected.getDescription(), r);
			}
			this.lastPopupItem = currentSelected;
		}
		// If the popup hasn't been made yet, or there's no popup
		if(this.statPopup == null || this.lastPopupItem == null) return;
		
		double mx = game.mouseSX();
		double my = game.mouseSY();
		if(!bounds.contains(mx, my)) return;
		
		double w = this.statPopup.getWidth();
		double sw = game.getScreenWidth();
		double sh = game.getScreenHeight();
		double h = this.statPopup.getHeight();
		
		double x = Math.max(0, mx - w * 0.5);
		if(x + w > sw) x = sw - w;
		double y = Math.max(0, my - h);
		if(y + h > sh) y = sh - h;
		
		this.statPopup.drawToRenderer(x, y, r);
	}
	
	/** @return See {@link #displayDecimals} */
	public boolean isDisplayDecimals(){
		return this.displayDecimals;
	}
	
	/** A buffer used to draw the popup */
	public static class StatPopup extends DrawableBuffer{
		
		/** The text to draw on the buffer */
		private final String text;
		
		/**
		 * @param description The text to split up into a block of text for the popup
		 * @param r The renderer to use for initially determining the size of the popup
		 */
		public StatPopup(String description, Renderer r){
			super(1, 1);
			
			if(description == null || description.isEmpty()) {
				this.text = null;
				return;
			}
			
			r.setFontSize(24);
			var textBounds = r.createTextBounds(description, 600);
			double extraSize = 12;
			
			// Width will be based on the maximum of the line widths
			double w = extraSize + textBounds.width();
			// Height is based on the line height and the total number of lines
			double h = extraSize + textBounds.height();
			this.regenerateBuffer((int)Math.round(w), (int)Math.round(h));
			this.text = textBounds.text();
		}
		
		@Override
		public void draw(Renderer r){
			super.draw(r);
			if(this.text == null) return;
			
			double w = this.getWidth();
			double h = this.getHeight();
			
			r.setColor(new ZColor(0));
			r.drawRectangle(0, 0, w, h);
			
			r.setColor(new ZColor(.8));
			r.drawRectangle(new ZRect(0, 0, w, h, -2));
			
			r.setColor(new ZColor(0));
			r.drawText(6, 25, this.text);
		}
	}
	
}
