package zusass.menu.spellmaker;

import zgame.core.sound.ManagedSoundSource;
import zgame.core.sound.SoundManager;
import zgame.menu.format.MultiFormatter;
import zgame.menu.format.PercentFormatter;
import zgame.menu.format.PixelFormatter;
import zusass.ZusassGame;
import zusass.menu.comp.ZusassButton;
import zusass.utils.ZusassSounds;

/** The button in the spell creation menu which performs the actual spell creation */
public class SpellCreateButton extends ZusassButton{
	
	/** The menu using this button */
	private final SpellMakerMenu menu;
	
	/** Source for the creation of a spell */
	private final ManagedSoundSource creationSound;
	
	/**
	 * Create a {@link SpellCreateButton} with the appropriate parameters
	 */
	public SpellCreateButton(SpellMakerMenu menu){
		super(0, 0, 200, 32, "Create");
		this.menu = menu;
		this.setFontSize(20);
		this.centerText();
		this.setFormatter(new MultiFormatter(new PixelFormatter(null, null, null, 40.0), new PercentFormatter(null, null, .5, null)));
		
		this.creationSound = new ManagedSoundSource(ZusassSounds.MAGIC_SOUND, null, 2, 2.5, 0.5);
	}
	
	@Override
	public void destroy(){
		super.destroy();
		this.creationSound.destroy();
	}
	
	@Override
	public void click(){
		super.click();
		
		var zgame = ZusassGame.get();
		var player = zgame.getPlayer();
		
		var spell = this.menu.createSpell();
		if(spell == null) return;
		
		player.getSpells().addSpell(spell);
		
		// Play a sound for the spell being made
		this.creationSound.setPosition(SoundManager.get().getListener());
		this.creationSound.playSound();
		
		var inventoryMenu = zgame.getPlayState().getSpellListMenu();
		inventoryMenu.regenerateThings();
		inventoryMenu.updateScrollAmount();
		
		this.menu.setTextBoxText(SpellMakerMenu.NAME, "");
	}
}
