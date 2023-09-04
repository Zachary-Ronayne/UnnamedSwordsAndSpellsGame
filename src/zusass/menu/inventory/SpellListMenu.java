package zusass.menu.inventory;

import org.lwjgl.glfw.GLFW;
import zgame.core.Game;
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
	 *
	 * @param zgame The game which will use this menu
	 */
	public SpellListMenu(ZusassGame zgame){
		super(zgame);
		this.setWidth(200);
		this.initMenuThings(zgame);
	}
	
	@Override
	public MenuThing getScrollableMovingThing(ZusassGame zgame){
		this.spellList = new SpellList(this, zgame);
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
	public void regenerateThings(ZusassGame zgame){
		this.spellList.generateButtons(this.getMob(), zgame);
	}
	
	@Override
	public void regenerateBuffer(){
		super.regenerateBuffer();
		this.spellList.regenerateBuffer();
	}
	
	@Override
	public void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.keyAction(game, button, press, shift, alt, ctrl);
		if(this == game.getCurrentState().getMenu() && !press && button == GLFW.GLFW_KEY_DELETE){
			var zgame = (ZusassGame)game;
			var mob = this.getMob();
			if(mob == null) return;
			var spells = mob.getSpells();
			spells.removeSelectedSpell();
			var size = spells.getSpellList().size();
			if(spells.getSelectedSpellIndex() >= size) spells.setSelectedSpellIndex(size - 1);
			game.onNextLoop(() -> this.regenerateThings(zgame));
		}
	}
}
