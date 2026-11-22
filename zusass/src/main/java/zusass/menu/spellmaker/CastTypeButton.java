package zusass.menu.spellmaker;

import zgame.menu.format.PixelFormatter;
import zusass.menu.comp.ZusassEnumToggleButton;

/** A button for selecting the spell cast type for the spell maker */
public class CastTypeButton extends ZusassEnumToggleButton<MakerCastType>{
	
	/** The menu using this button */
	private final SpellMakerMenu menu;
	
	/**
	 * @param menu See {@link #menu}
	 */
	public CastTypeButton(SpellMakerMenu menu){
		super(0, 0, 180, 32, MakerCastType.PROJECTILE, MakerCastType.values());
		this.menu = menu;
		this.onValueChange(this.getSelectedValue());
		
		this.setFontSize(20);
		this.setFormatter(new PixelFormatter(420.0, null, null, 200.0));
	}
	
	@Override
	public void onValueChange(MakerCastType value){
		super.onValueChange(value);
		
		if(this.menu != null) menu.updateDisplayedFields(value.getCastType());
	}
}
