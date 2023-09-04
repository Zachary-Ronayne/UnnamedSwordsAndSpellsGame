package zusass.menu.player;

import zgame.core.Game;
import zgame.menu.MenuThing;
import zgame.menu.format.MenuFormatter;
import zgame.menu.format.MultiFormatter;
import zgame.menu.format.PercentFormatter;
import zgame.menu.format.PixelFormatter;
import zusass.ZusassGame;

/** The menu which displays on top of the game */
public class StatsMenu extends DraggableMenu{
	
	// TODO add a timer to update all the stats, something like once a second
	
	/** The list displaying the stats */
	private StatList statList;
	
	/**
	 * Create a new {@link StatsMenu} for displaying the spells of something
	 *
	 * @param zgame The game which will use this menu
	 */
	public StatsMenu(ZusassGame zgame){
		super(zgame);
		this.setWidth(200);
		this.initMenuThings(zgame);
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
		// TODO make the height in the stat list properly update so scrolling works
		return this.statList.getHeight();
	}
	
	@Override
	public void onAdd(Game game){
		super.onAdd(game);
	}
	
	@Override
	public void regenerateThings(ZusassGame zgame){
		this.statList.regenerateText(zgame, this.getMob());
	}
}
