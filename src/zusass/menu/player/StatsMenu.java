package zusass.menu.player;

import org.lwjgl.glfw.GLFW;
import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.utils.ZRect;
import zgame.core.utils.ZStringUtils;
import zgame.menu.MenuThing;
import zgame.menu.format.MenuFormatter;
import zgame.menu.format.MultiFormatter;
import zgame.menu.format.PercentFormatter;
import zgame.menu.format.PixelFormatter;
import zusass.ZusassGame;

import java.util.ArrayList;

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
		
		// TODO make the selected stat have a highlight when the mouse is over it
		var selected = this.statList.getSelectedStat();
		if(selected == null) return;
		var text = selected.getDescription();
		if(text == null || text.isEmpty()) return;
		
		// TODO make this generate a buffer, like as a separate text buffer, not a menu thing
		// TODO abstract this out into another class
		r.setFontSize(24);
		var font = r.getFont();
		if(font == null) return;
		
		// Max width of the popup
		double maxWidth = 600;
		// Largest width of a word
		double largestWidth = 0;
		
		// Find the width of each word
		var words = text.split("\\s+");
		var sizes = new double[words.length];
		for(int i = 0; i < words.length; i++){
			var word = words[i];
			var strW = font.stringWidth(word);
			if(strW > maxWidth) maxWidth = strW;
			if(strW > largestWidth) largestWidth = strW;
			sizes[i] = strW;
		}
		var lines = new ArrayList<String>();
		
		// Find how many lines to use based on the max width
		var currentWidth = 0;
		var sb = new StringBuilder();
		var spaceWidth = font.charWidth(' ');
		var largestLine = 0;
		
		// Go through each word
		var first = true;
		for(int i = 0; i < words.length; i++){
			var word = words[i];
			var wordWidth = sizes[i];
			// If adding the next word would exceed the current line width, add it to a new line
			var newLine = currentWidth + wordWidth >= maxWidth;
			if(newLine){
				lines.add(sb.toString());
				if(currentWidth > largestLine) largestLine = currentWidth;
				currentWidth = 0;
				sb = new StringBuilder();
			}
			// Otherwise, if this is not the first line, add a space after the word
			else{
				if(!first){
					sb.append(" ");
					currentWidth += spaceWidth;
				}
				else first = false;
			}
			// Add the word to the current line
			sb.append(word);
			currentWidth += wordWidth;
		}
		// If anything is left in the string builder, add another line
		var remaining = sb.toString();
		if(!remaining.isEmpty()){
			lines.add(remaining);
			if(currentWidth > largestLine) largestLine = currentWidth;
		}
		
		double extraSize = 12;
		double lineCount = lines.size();
		
		// Width will be based on the maximum of the line widths
		double w = extraSize + largestLine;
		// Height is based on the line height and the total number of lines
		double h = extraSize + lineCount * font.getMaxHeight() + r.getFontCharSpace() * (lineCount - 1);
		double x = game.mouseSX();
		double y = game.mouseSY() - h;
		if(!bounds.contains(x, y)) return;
		
		r.setColor(new ZColor(0));
		r.drawRectangle(x, y, w, h);
		
		r.setColor(new ZColor(.8));
		r.drawRectangle(new ZRect(x, y, w, h, -2));
		
		r.setColor(new ZColor(0));
		r.drawText(x + 6, y + 25, ZStringUtils.concatSep("\n", lines.toArray()));
	}
	
	/** @return See {@link #displayDecimals} */
	public boolean isDisplayDecimals(){
		return this.displayDecimals;
	}
}
