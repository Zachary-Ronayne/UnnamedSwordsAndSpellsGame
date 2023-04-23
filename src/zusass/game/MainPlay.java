package zusass.game;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.state.MenuNode;
import zgame.core.state.PlayState;
import zusass.ZusassGame;
import zusass.game.stat.ZusassStat;
import zusass.game.things.entities.mobs.ZusassPlayer;
import zusass.menu.inventory.InventoryMenu;
import zusass.menu.mainmenu.MainMenuState;
import zusass.menu.pause.PauseMenu;

import static org.lwjgl.glfw.GLFW.*;

/**
 * The main {@link PlayState} used by the Zusass game
 * I initially called this ZusassPlay, but I um... changed it
 */
public class MainPlay extends PlayState{
	
	/** The {@link PauseMenu} to display */
	private final PauseMenu pauseMenu;
	/** The {@link InventoryMenu} to display */
	private final InventoryMenu inventoryMenu;
	/** true if the inventory menu is being shown, false otherwise */
	private boolean showingInventory;
	
	/**
	 * Initialize the main play state for the Zusass game
	 *
	 * @param zgame The {@link Game} using this state
	 */
	public MainPlay(ZusassGame zgame){
		this.enterHub(zgame);
		
		this.pauseMenu = new PauseMenu(zgame);
		this.inventoryMenu = new InventoryMenu(zgame);
		this.showingInventory = false;
	}
	
	/**
	 * Set the current room of the game to the main hub
	 *
	 * @param zgame The {@link Game} using this state
	 */
	public void enterHub(ZusassGame zgame){
		// Make the hub and set that as the current room
		Hub hub = new Hub(zgame);
		this.setCurrentRoom(hub);
		
		// Place the player on the next tick
		hub.onNextTick(() -> {
			ZusassPlayer player = zgame.getPlayer();
			player.setX(20);
			player.setY(hub.maxY() - player.getHeight());
			player.setLockCamera(true);
			hub.addThing(player);
			player.centerCamera(zgame);
		});
	}
	
	@Override
	public ZusassRoom getCurrentRoom(){
		return (ZusassRoom)super.getCurrentRoom();
	}
	
	/**
	 * Set the current state of the game to the main menu
	 *
	 * @param zgame The {@link Game} using this state
	 */
	public void enterMainMenu(ZusassGame zgame){
		zgame.setCurrentState(new MainMenuState(zgame));
	}
	
	@Override
	public void playKeyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.playKeyAction(game, button, press, shift, alt, ctrl);
		if(press) return;
		
		// On releasing escape, open the pause menu, or close the pause or inventory menu
		if(button == GLFW_KEY_ESCAPE){
			ZusassGame zgame = (ZusassGame)game;
			// If the game is not paused, but the inventory is showing, close the inventory
			if(!zgame.getPlayState().isPaused() && this.isShowingInventory()) this.closeInventory();
			// Otherwise, open the pause menu
			else this.openPauseMenu(zgame);
		}
		// On releasing tab, open inventory if it is not open, otherwise, close it
		else if(button == GLFW_KEY_TAB){
			if(this.showingInventory) this.closeInventory();
			else this.openInventory((ZusassGame)game);
		}
	}
	
	@Override
	public boolean playMouseAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		boolean input = super.playMouseAction(game, button, press, shift, alt, ctrl);
		if(input) return true;
		var zgame = (ZusassGame)game;
		return zgame.getPlayer().mouseAction(zgame, button, press, shift, alt, ctrl);
	}
	
	@Override
	public void renderBackground(Game game, Renderer r){
		super.renderBackground(game, r);
		
		// Draw a solid color for the background
		r.setColor(new ZColor(.05));
		r.drawRectangle(0, 0, game.getScreenWidth(), game.getScreenHeight());
		
		// Draw the rest of the background
		super.renderBackground(game, r);
	}
	
	@Override
	public void renderHud(Game game, Renderer r){
		var zgame = (ZusassGame)game;
		
		// First draw the in game hud
		
		// Draw a basic health bar
		ZusassPlayer p = zgame.getPlayer();
		if(p == null) return;
		this.drawResourceBar(r, p, ZusassStat.HEALTH, ZusassStat.HEALTH_MAX, 0, new ZColor(1, 0, 0), new ZColor(1));
		this.drawResourceBar(r, p, ZusassStat.STAMINA, ZusassStat.STAMINA_MAX, 1, new ZColor(0, 1, 0), new ZColor(.2));
		this.drawResourceBar(r, p, ZusassStat.MANA, ZusassStat.MANA_MAX, 2, new ZColor(0, 0, 1), new ZColor(1));
		
		// Using draw text like this is inefficient, but whatever, this is temp code
		String text;
		if(p.isCasting()){
			var spell = p.getSelectedSpell();
			var name = spell.getName();
			text = name == null ? "No Spell" : "Spell: " + spell.nameAndCost();
		}
		else text = "Attack";
		
		// Draw the name of the selected spell or that the player is in attack mode
		var w = r.getFont().stringWidth(text);
		r.setColor(0, 0, 0, .5);
		r.drawRectangle(10, 85, w, 20);
		r.setColor(1, 1, 1, 1);
		r.drawText(10, 100, text);
		
		// Now draw the rest of the menus
		super.renderHud(game, r);
	}
	
	/** Temporary code for simplicity of testing */
	private void drawResourceBar(Renderer r, ZusassPlayer p, ZusassStat current, ZusassStat max, int index, ZColor color, ZColor textColor){
		var c = p.stat(current);
		var m = p.stat(max);
		var space = 25 * index;
		
		r.setColor(.5, .5, .5);
		r.drawRectangle(10, 10 + space, 200, 20);
		r.setColor(color);
		r.drawRectangle(10, 10 + space, 200 * c / m, 20);
		// Using draw text like this is inefficient, but whatever, this is temp code
		r.setColor(textColor);
		r.setFontSize(20);
		r.drawText(10, 28 + space, Math.round(Math.max(0, c)) + " / " + Math.round(m));
	}
	
	/**
	 * Open the pause menu for the game, and pause the game
 	 * @param zgame The game to pause and open the pause menu
	 */
	public void openPauseMenu(ZusassGame zgame){
		MenuNode pauseNode = MenuNode.withAll(this.pauseMenu);
		zgame.getPlayState().fullPause();
		this.popupMenu(pauseNode);
	}
	
	/** Close the display of the player inventory */
	public void closeInventory(){
		if(!this.showingInventory) return;
		this.showingInventory = false;
		this.removeTopMenu(false);
	}
	
	/**
	 * Make the player inventory menu show
	 * @param zgame The game to show the inventory in
	 */
	public void openInventory(ZusassGame zgame){
		if(showingInventory) return;
		this.showingInventory = true;
		this.inventoryMenu.setMob(zgame.getPlayer());
		this.inventoryMenu.regenerateThings(zgame);
		this.inventoryMenu.regenerateBuffer();
		this.popupMenu(MenuNode.withAll(this.inventoryMenu));
	}
	
	/** @return See {@link #pauseMenu} */
	public PauseMenu getPauseMenu(){
		return this.pauseMenu;
	}
	
	/** @return See {@link #inventoryMenu} */
	public InventoryMenu getInventoryMenu(){
		return this.inventoryMenu;
	}
	
	/** @return See {@link #showingInventory} */
	public boolean isShowingInventory(){
		return this.showingInventory;
	}
}
