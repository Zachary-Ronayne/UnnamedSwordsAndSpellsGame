package zusass.menu.player;

import zgame.core.graphics.TextOption;
import zusass.ZusassGame;
import zusass.game.stat.ZusassStat;
import zusass.game.things.entities.mobs.ZusassMob;

import java.util.ArrayList;

/** An item holding text for the {@link StatList}, for an attribute stat */
public class AttributeListItem extends StatListItem{
	
	/** The name used to describe this stat */
	private final String baseName;

	/**
	 * Create a new stat item
	 * @param size The size of this item
	 * @param statList See {@link #statList}
	 * @param baseName See {@link #baseName}
	 * @param zgame The game to use to create the item
	 */
	public AttributeListItem(double size, StatList statList, String baseName, ZusassStat statType, ZusassGame zgame){
		super(size, statList, statType, zgame);
		this.baseName = baseName;
	}
	
	@Override
	public void updateTextOptions(ZusassMob mob){
		var options = new ArrayList<TextOption>(2);
		options.add(new TextOption(new StringBuilder(this.baseName).append(": ").toString(), BASE_TEXT_COLOR));
		options.add(this.makeTextOption(mob.getStat(this.getStatType())));
		
		this.getTextBuffer().setOptions(options);
	}
}
