package zusass.menu.player;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.utils.ZRect;
import zusass.ZusassGame;
import zusass.game.stat.ZusassStat;
import zusass.game.things.entities.mobs.ZusassMob;
import zusass.menu.comp.ZusassMenuText;

import java.text.DecimalFormat;

/** An item holding text for the {@link StatList} */
public class StatListItem extends ZusassMenuText{

	/** The stat which should be displayed on this text thing, or null if the text should not be updated by a stat */
	private final ZusassStat statType;
	/** true if the next stat should move to a new line after this is rendered, false otherwise */
	private final boolean newLine;
	
	/**
	 * Create a new stat item
	 * @param statType See {@link #statType}
	 * @param newLine See {@link #newLine}
	 * @param zgame The game to use to create the item
	 */
	public StatListItem(ZusassStat statType, boolean newLine, ZusassGame zgame){
		// TODO make constants for the magic numbers
		super(0, 0, 100, 26, "", zgame);
		this.statType = statType;
		this.newLine = newLine;
		this.setFontColor(new ZColor(0));
		this.setFill(new ZColor(1));
		this.removeBorder();
		this.setBorderWidth(0);
		this.setFontSize(22);
	}
	
	/**
	 * Update the text of this stat
	 * @param previous The item which goes before this one on the x axis, or null if none go before it
	 * @param mob The mob to get the stat from
	 */
	public void updateText(StatListItem previous, ZusassMob mob){
		// Update the text
		if(this.getStatType() != null){
			var decimalFormat = new DecimalFormat("0.00");
			var stat = mob.getStat(statType);
			this.setText(decimalFormat.format(stat.get()));
			
			// Update color
			if(stat.buffed()) this.setFontColor(new ZColor(0, .5, 0));
			else if(stat.debuffed()) this.setFontColor(new ZColor(.5, 0, 0));
			else this.setFontColor(new ZColor(0));
		}
		// TODO update width depending on string length
		this.centerTextVertical();
		
		// TODO Update x position
		if(previous == null) this.alignTextXLeft(4);
		else this.setRelX(previous.getRelX() + previous.getWidth());
	}
	
	/** @return See {@link #statType} */
	public ZusassStat getStatType(){
		return this.statType;
	}
	
	/** @return See {@link #newLine} */
	public boolean isNewLine(){
		return this.newLine;
	}
	
	@Override
	public void render(Game game, Renderer r, ZRect bounds){
		super.render(game, r, bounds);
	}
}
