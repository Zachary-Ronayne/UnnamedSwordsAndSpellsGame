package zusass.menu.spellmaker;

import zgame.core.Game;
import zgame.menu.format.PixelFormatter;
import zusass.menu.comp.ZusassEnumToggleButton;

/** A button for selecting the stat effect type for a spell in the spell maker */
public class ModifierTypeButton extends ZusassEnumToggleButton<MakerModifierType>{
	
	/** The menu using this button */
	private final SpellMakerMenu menu;
	
	/**
	 * @param menu See {@link #menu}
	 */
	public ModifierTypeButton(SpellMakerMenu menu){
		super(0, 0, 180, 32, MakerModifierType.ADD, MakerModifierType.values());
		this.menu = menu;
		this.onValueChange(this.getSelectedValue());
		
		this.setFontSize(20);
		this.setFormatter(new PixelFormatter(820.0, null, null, 200.0));
	}
	
	@Override
	public void onValueChange(MakerModifierType value){
		if(this.menu != null) this.menu.updateModifierType(value.getModifierType());
		
		super.onValueChange(value);
	}
	
	@Override
	public void click(Game game){
		super.click(game);
		this.menu.updatePositiveNegativeButton();
	}
}
