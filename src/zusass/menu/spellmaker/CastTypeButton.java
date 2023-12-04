package zusass.menu.spellmaker;

import zgame.menu.format.PixelFormatter;
import zusass.ZusassGame;
import zusass.game.magic.SpellCastType;
import zusass.menu.comp.ToggleButton;

import java.util.ArrayList;
import java.util.Map;

/** A button for selecting the spell cast type for the spell maker */
public class CastTypeButton extends ToggleButton{
	
	/** The text for selecting the projectile cast type */
	public static final String PROJECTILE = "Projectile";
	/** The text for selecting the self cast type */
	public static final String SELF = "Self";
	
	/** Mapping the spell cast type to the text used for selecting the cast type */
	private static final Map<String, SpellCastType> CAST_TYPE_MAP = Map.of(PROJECTILE, SpellCastType.PROJECTILE, SELF, SpellCastType.SELF);
	
	/** The menu using this button */
	private final SpellMakerMenu menu;
	
	/**
	 * @param menu See {@link #menu}
	 * @param zgame The {@link ZusassGame} that uses this button
	 */
	public CastTypeButton(SpellMakerMenu menu, ZusassGame zgame){
		super(0, 0, 180, 32, null, zgame);
		this.menu = menu;
		
		this.setFontSize(20);
		this.setFormatter(new PixelFormatter(420.0, null, null, 200.0));
		
		var values = new ArrayList<String>();
		values.add(PROJECTILE);
		values.add(SELF);
		this.setValues(values);
		this.setSelectedIndex(0);
	}
	
	// TODO do this in a better way, so not updating things based on a string map?
	@Override
	public void setText(String text){
		super.setText(text);
		menu.updateDisplayedFields(CAST_TYPE_MAP.get(text));
		this.centerText();
	}
}
