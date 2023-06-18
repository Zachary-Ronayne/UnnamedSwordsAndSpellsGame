package zusass.menu.spellmaker;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.menu.format.MultiFormatter;
import zgame.menu.format.PercentFormatter;
import zgame.menu.format.PixelFormatter;
import zusass.ZusassGame;
import zusass.menu.ZusassMenu;
import zusass.menu.mainmenu.comp.newgamemenu.ZusassTextBox;

/** The {@link ZusassMenu} for creating spells */
public class SpellMakerMenu extends ZusassMenu{
	
	/** The text box holding the user entered name for the spell */
	private final ZusassTextBox spellNameTextBox;
	
	/**
	 * Create the menu
	 *
	 * @param zgame The game using the menu
	 */
	public SpellMakerMenu(ZusassGame zgame){
		super(zgame, "Spell Creation");
		
		this.setFill(new ZColor(.4, 0, .6, .8));
		this.setBorder(new ZColor(.3, 0, .5, .5));
		this.setDraggableColor(new ZColor(.6, 0, .85, .8));
		this.makeDraggable(10, 30);
		this.setMinWidth(600.0);
		this.setMinHeight(400.0);
		
		this.getTitleThing().setFontSize(40);
		this.getTitleThing().setTextY(50);
		
		this.format(zgame.getWindow(), new PercentFormatter(.8, .95, .5, .5));
		this.reformat(zgame);
		
		// The button for creating a new spell
		// TODO add an inherent way to disable a button or any input menu thing, and make this button disabled if a spell name is not set
		var createButton = new SpellCreateButton(this, zgame);
		this.addThing(createButton);
		
		// TODO disable all other input when the text box is selected
		this.spellNameTextBox = new ZusassTextBox(0, 120, 1, 50, zgame);
		this.spellNameTextBox.setHint("Enter a name...");
		this.spellNameTextBox.setFormatter(new PercentFormatter(.9, null, 0.5, null));
		this.addThing(this.spellNameTextBox);
		
		// TODO make a way of abstracting this out so that you don't have to do this when something gets resized
		
		// TODO add a button to reset the state of the menu, like clear all selections
		
		// TODO make buttons not highlight on mouse hover if they are not currently clickable
	}
	
	/**
	 * Format the components of this menu based on its current size
	 * @param zgame The game to reformat to
	 */
	private void reformat(ZusassGame zgame){
		var t = this.getTitleThing();
		t.format(new MultiFormatter(new PercentFormatter(1.0, 1.0, 0.5, 0.5), new PixelFormatter(null, null, 50.0, null)));
		t.centerTextHorizontal();
	}
	
	@Override
	public void onDragEnd(Game game, boolean sideDrag){
		// TODO fix the title buffer not being fully cleared when it's resized
		super.onDragEnd(game, sideDrag);
		this.reformat((ZusassGame)game);
	}
	
	@Override
	public boolean isDefaultDestroyRemove(){
		return false;
	}
	
	/** @return The text the user has input for the spell name */
	public String getEnteredName(){
		return this.spellNameTextBox.getText();
	}
	
	// TODO clear the contents of the menu after clicking create
	
}
