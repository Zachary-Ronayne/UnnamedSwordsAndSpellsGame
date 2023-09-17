package zusass.menu.player;

import org.lwjgl.glfw.GLFW;
import zgame.core.Game;
import zgame.menu.MenuThing;
import zgame.menu.format.MenuFormatter;
import zgame.menu.format.MultiFormatter;
import zgame.menu.format.PercentFormatter;
import zgame.menu.format.PixelFormatter;
import zusass.ZusassGame;

/** The menu which displays on top of the game */
public class StatsMenu extends DraggableMenu{
	
	// TODO add a popup showing a description for each stat
	
	/** The list displaying the stats */
	private StatList statList;
	
	/** The number of seconds to wait between stat updates */
	public static final double STAT_UPDATE_TIME = 0.5;
	
	/** The amount of time, in seconds, since the last time stats were updated */
	private double lastStatUpdate;
	
	/** true to display decimal places on stats, false otherwise */
	private boolean displayDecimals;
	
	/**
	 * Create a new {@link StatsMenu} for displaying the spells of something
	 *
	 * @param zgame The game which will use this menu
	 */
	public StatsMenu(ZusassGame zgame){
		super(zgame);
		this.lastStatUpdate = 0;
		this.setWidth(200);
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
		// TODO fix the annoying blinking that comes from updating stats
		this.statList.regenerateText(zgame, this.getMob());
		this.lastStatUpdate = 0;
	}
	
	@Override
	public void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.keyAction(game, button, press, shift, alt, ctrl);
		if(button != GLFW.GLFW_KEY_LEFT_SHIFT && button != GLFW.GLFW_KEY_RIGHT_SHIFT) return;
		
		this.displayDecimals = press;
		this.regenerateThings((ZusassGame)game);
	}
	
	@Override
	public void tick(Game game, double dt){
		super.tick(game, dt);
		if(this.lastStatUpdate >= STAT_UPDATE_TIME) this.regenerateThings((ZusassGame)game);
		else this.lastStatUpdate += dt;
	}
	
	/** @return See {@link #displayDecimals} */
	public boolean isDisplayDecimals(){
		return this.displayDecimals;
	}
}
