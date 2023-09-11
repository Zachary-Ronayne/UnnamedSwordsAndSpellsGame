package zusass.menu.player;

import zgame.core.graphics.TextOption;
import zgame.core.graphics.ZColor;
import zgame.core.utils.ZArrayUtils;
import zgame.stat.Stat;
import zusass.ZusassGame;
import zusass.game.stat.ZusassStat;
import zusass.game.things.entities.mobs.ZusassMob;
import zusass.menu.comp.ZusassMenuText;

import java.text.DecimalFormat;

/** An item holding text for the {@link StatList} */
public class StatListItem extends ZusassMenuText{
	
	/** A formatter used to display text for these items */
	public static final DecimalFormat NUMBER_FORMATTER = new DecimalFormat("0.00");
	
	/** The stat which should be displayed on this text thing, or null if the text should not be updated by a stat */
	private final ZusassStat statType;
	
	/**
	 * Create a new stat item
	 *
	 * @param statType See {@link #statType}
	 * @param zgame The game to use to create the item
	 */
	public StatListItem(ZusassStat statType, ZusassGame zgame){
		// TODO make constants for the magic numbers
		super(0, 0, 1000, 26, "", zgame);
		this.statType = statType;
		
		this.setFill(new ZColor(1));
		this.removeBorder();
		this.setBorderWidth(0);
		this.setFontSize(22);
	}
	
	/**
	 * Update the text of this stat
	 *
	 * @param mob The mob to get the stat from
	 */
	public void updateText(ZusassMob mob){
		this.updateTextOptions(mob);
		this.centerTextVertical();
		
		// Update x position
		this.alignTextXLeft(4);
	}
	
	/** @return See {@link #statType} */
	public ZusassStat getStatType(){
		return this.statType;
	}
	
	/**
	 * Based on the given mob, update the text options to display for this item based on the mob's stats
	 *
	 * @param mob The mob to get stats from
	 */
	public void updateTextOptions(ZusassMob mob){
		var type = this.getStatType();
		if(type == null) return;
		
		this.getTextBuffer().setOptions(ZArrayUtils.singleList(makeTextOption(mob.getStat(type))));
	}
	
	/**
	 * Generate the text option for a specific stat
	 *
	 * @param stat The stat to make a text option for
	 * @return The text option
	 */
	public static TextOption makeTextOption(Stat stat){
		var text = NUMBER_FORMATTER.format(stat.get());
		ZColor color;
		
		// Update color
		if(stat.buffed()) color = new ZColor(0, .5, 0);
		else if(stat.debuffed()) color = new ZColor(.5, 0, 0);
		else color = new ZColor(0);
		return new TextOption(text, color);
	}
}
