package zusass.menu.spellmaker;

import zgame.menu.format.MultiFormatter;
import zgame.menu.format.PercentFormatter;
import zgame.menu.format.PixelFormatter;
import zusass.ZusassGame;
import zusass.menu.comp.ZusassButton;

/** The button in the spell creation menu which performs the actual spell creation */
public class SpellCreateButton extends ZusassButton{
	
	/** The menu using this button */
	private final SpellMakerMenu menu;
	
	/**
	 * Create a {@link SpellCreateButton} with the appropriate parameters
	 */
	public SpellCreateButton(SpellMakerMenu menu){
		super(0, 0, 200, 32, "Create");
		this.menu = menu;
		this.setFontSize(20);
		this.centerText();
		this.setFormatter(new MultiFormatter(new PixelFormatter(null, null, null, 40.0), new PercentFormatter(null, null, .5, null)));
	}
	
	@Override
	public void click(){
		super.click();
		
		var zgame = ZusassGame.get();
		var player = zgame.getPlayer();
		
		var spell = this.menu.createSpell();
		if(spell == null) return;
		
		player.getSpells().addSpell(spell);
		
		var inventoryMenu = zgame.getPlayState().getSpellListMenu();
		inventoryMenu.regenerateThings();
		inventoryMenu.updateScrollAmount();
		
		this.menu.setTextBoxText(SpellMakerMenu.NAME, "");
	}
}
