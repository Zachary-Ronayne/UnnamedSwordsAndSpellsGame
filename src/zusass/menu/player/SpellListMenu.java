package zusass.menu.player;

import org.lwjgl.glfw.GLFW;
import zgame.menu.MenuThing;
import zgame.menu.format.MenuFormatter;
import zgame.menu.format.MultiFormatter;
import zgame.menu.format.PercentFormatter;
import zgame.menu.format.PixelFormatter;
import zusass.ZusassGame;

/** The menu which displays on top of the game */
public class SpellListMenu extends DraggableMenu{
	
	/** The menu thing holding the list of spells to display */
	private SpellList spellList;
	
	/**
	 * Create a new {@link SpellListMenu} for displaying the spells of something
	 */
	public SpellListMenu(){
		super();
		this.setWidth(200);
		this.initMenuThings();
	}
	
	@Override
	public MenuThing getScrollableMovingThing(){
		this.spellList = new SpellList(this);
		return this.spellList;
	}
	
	@Override
	public MenuFormatter getDefaultFormatter(){
		return new MultiFormatter(new PixelFormatter(null, 10.0, null, null), new PercentFormatter(null, 0.8, null, 0.5));
	}
	
	@Override
	public double getScrollWheelStrength(){
		return (SpellListButton.HEIGHT + SpellListButton.HEIGHT_SPACE) * 0.5;
	}
	
	@Override
	public double getFullScrollableSize(){
		return this.spellList.getHeight();
	}
	
	@Override
	public void regenerateThings(){
		this.spellList.generateButtons(this.getMob());
	}
	
	@Override
	public void regenerateBuffer(){
		super.regenerateBuffer();
		this.spellList.regenerateBuffer();
	}
	
	@Override
	public void keyActionFocused(int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.keyActionFocused(button, press, shift, alt, ctrl);
		var zgame = ZusassGame.get();
		if(this == zgame.getCurrentState().getMenu() && !press && button == GLFW.GLFW_KEY_DELETE){
			var mob = this.getMob();
			if(mob == null) return;
			var spells = mob.getSpells();
			spells.removeSelectedSpell();
			var size = spells.getSpellList().size();
			if(spells.getSelectedSpellIndex() >= size) spells.setSelectedSpellIndex(size - 1);
			zgame.onNextLoop(this::regenerateThings);
		}
	}
}
