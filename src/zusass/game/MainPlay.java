package zusass.game;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.state.MenuNode;
import zgame.core.state.PlayState;
import zgame.core.utils.ZMath;
import zgame.world.Direction3D;
import zgame.world.Room3D;
import zusass.ZusassGame;
import zusass.game.things.entities.mobs.ZusassPlayer;
import zusass.menu.player.SpellListMenu;
import zusass.menu.player.StatsMenu;
import zusass.menu.mainmenu.MainMenuState;
import zusass.menu.pause.PauseMenu;

import java.text.DecimalFormat;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

/**
 * The main {@link PlayState} used by the Zusass game
 * I initially called this ZusassPlay, but I um... changed it
 */
public class MainPlay extends PlayState{
	
	/** The {@link PauseMenu} to display */
	private final PauseMenu pauseMenu;
	/** The {@link SpellListMenu} to display */
	private final SpellListMenu spellListMenu;
	/** The {@link StatsMenu} to display */
	private final StatsMenu statsMenu;
	
	/** true if the player menus, i.e. stats, spells, etc, should be open, false otherwise */
	private boolean playerMenusOpen;
	
	/** true to display debug data on screen, false otherwise */
	private boolean debugInfo;
	
	/** A formatter for displaying numbers for debug information */
	private final DecimalFormat debugNumberFormat;
	
	/**
	 * Initialize the main play state for the Zusass game
	 */
	public MainPlay(){
		// issue#53 maybe make this go to a hub initially right away instead of a separate method call
		super(new Room3D(0, 0, 0));
		this.debugInfo = false;
		this.debugNumberFormat = new DecimalFormat("0.0#####");
		
		this.enterHub();
		
		this.playerMenusOpen = false;
		this.pauseMenu = new PauseMenu();
		this.spellListMenu = new SpellListMenu();
		this.statsMenu = new StatsMenu();
	}
	
	/**
	 * Set the current room of the game to the main hub
	 */
	public void enterHub(){
		// Make the hub and set that as the current room
		ZusassGame.get().onNextLoop(() -> {
			Hub hub = new Hub();
			this.setCurrentRoom(hub);
			
			// Place the player on the next tick
			hub.onNextTick(hub::placePlayer);
		});
	}
	
	@Override
	public void onSet(){
		super.onSet();
		ZusassGame.get().onNextLoop(() -> {
			var zgame = ZusassGame.get();
			this.getSpellListMenu().setMob(zgame.getPlayer());
			this.getStatsMenu().setMob(zgame.getPlayer());
		});
	}
	
	@Override
	public ZusassRoom getCurrentRoom(){
		return (ZusassRoom)super.getCurrentRoom();
	}
	
	/**
	 * Set the current state of the game to the main menu
	 */
	public void enterMainMenu(){
		ZusassGame.get().setCurrentState(new MainMenuState());
	}
	
	/** @return See {@link #playerMenusOpen} */
	public boolean isPlayerMenusOpen(){
		return this.playerMenusOpen;
	}
	
	@Override
	public void playKeyAction(int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.playKeyAction(button, press, shift, alt, ctrl);
		if(press) return;
		
		// On releasing escape, open the pause menu if no menus are open, or close whatever other menu is open
		if(button == GLFW_KEY_ESCAPE){
			// If the player menus are open, and one of them is on top, prioritize closing them
			if(this.playerMenusOpen && this.anyPlayerMenuOnTop()){
				this.closePlayerMenus();
				return;
			}
			// Otherwise close the top menu
			var c = ZusassGame.get().getCurrentState();
			if(c.getStackSize() > 0){
				c.removeTopMenu();
			}
			// Otherwise, open the pause menu
			else{
				this.openPauseMenu();
			}
		}
		// On releasing tab, open inventory if it is not open, otherwise, close it
		else if(button == GLFW_KEY_TAB){
			if(this.playerMenusOpen) this.closePlayerMenus();
			else this.openPlayerMenus();
		}
		// Use F3 to toggle debug
		else if(button == GLFW_KEY_F3) this.debugInfo = !this.debugInfo;
	}
	
	@Override
	public boolean playMouseAction(int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		boolean input = super.playMouseAction(button, press, shift, alt, ctrl);
		if(input) return true;
		var zgame = ZusassGame.get();
		return zgame.getPlayer().mouseAction(button, press, shift, alt, ctrl);
	}
	
	@Override
	public void renderBackground(Renderer r){
		super.renderBackground(r);
		
		// Draw a solid color for the background
		r.setColor(new ZColor(.05));
		var game = Game.get();
		r.drawRectangle(0, 0, game.getScreenWidth(), game.getScreenHeight());
		
		// Draw the rest of the background
		super.renderBackground(r);
	}
	
	@Override
	public void renderHud(Renderer r){
		var zgame = ZusassGame.get();
		
		// First draw the in game hud
		
		// Draw a basic health bar
		ZusassPlayer p = zgame.getPlayer();
		if(p == null) return;
		p.drawResourceBars(r, 8, 10, 200, 24, true);
		
		// Using draw text like this is inefficient, but whatever, this is temp code
		String text;
		if(p.isCasting()){
			var spell = p.getSelectedSpell();
			var name = spell.getName();
			text = name == null ? "No Spell" : "Spell: " + spell.nameAndCost();
		}
		else text = "Attack";
		
		// Draw the name of the selected spell or that the player is in attack mode
		r.setFontSize(20);
		drawTextBox(r, 10, 85, text, 1);
		
		// Draw the debug info of the player
		if(debugInfo){
			r.setFontSize(22);
			var mobilityData = p.getMobilityData();
			double ty = ZusassGame.window().getHeight() - 5;
			var yaw = mobilityData.getFacingYaw();
			var velocity = p.getVelocity();
			var debugTextList = List.of(
					"X: " + this.debugNumberFormat.format(p.getX()),
					"Y: " + this.debugNumberFormat.format(p.getY()),
					"Z: " + this.debugNumberFormat.format(p.getZ()),
					"VX: " + this.debugNumberFormat.format(velocity.getX()),
					"VY: " + this.debugNumberFormat.format(velocity.getY()),
					"VZ: " + this.debugNumberFormat.format(velocity.getZ()),
					"YAW: " + this.debugNumberFormat.format(Math.toDegrees(ZMath.angleNormalized(yaw))),
					"PIT: " + this.debugNumberFormat.format(Math.toDegrees(ZMath.angleNormalized(mobilityData.getFacingPitch()))),
					"FAC: " + Direction3D.findCardinal(yaw).name()
			);
			double border = 1;
			for(int i = debugTextList.size() - 1; i >= 0; i--){
				drawTextBox(r, 5, ty -= (r.getFontSize() + border * 2), debugTextList.get(i), border);
			}
		}
		
		// Draw a cross-hair on the center
		this.drawCrossHair(r);
		
		// Now draw the rest of the menus
		super.renderHud(r);
	}
	
	/** @param r The renderer to use to draw the cross-hair */
	private void drawCrossHair(Renderer r){
		double size = 8;
		double thick = 2;
		double border = 1;
		var buffer = ZusassGame.window().getWindowBuffer();
		double centerX = buffer.getWidth() * 0.5;
		double centerY = buffer.getHeight() * 0.5;
		
		r.setColor(0, 0, 0);
		r.drawRectangle(centerX - size, centerY - thick, size + size, thick + thick);
		r.drawRectangle(centerX - thick, centerY - size, thick + thick, size + size);
		r.setColor(0.7, 0.7, 0.7);
		r.drawRectangle(centerX - size + border, centerY - thick + border, size + size - border - border, thick + thick - border - border);
		r.drawRectangle(centerX - thick + border, centerY - size + border, thick + thick - border - border, size + size - border - border);
	}
	
	/**
	 * Drw a basic text box for showing info in the hud
	 *
	 * @param x The x position to draw the text
	 * @param y The y position to draw the text
	 * @param r The renderer to draw with
	 * @param border The number of pixes to add on every side of the text
	 * @param text The text to display
	 */
	private void drawTextBox(Renderer r, double x, double y, String text, double border){
		double fontSize = r.getFontSize();
		r.setFontSize(fontSize);
		var w = r.getFont().stringWidth(text);
		r.setColor(0, 0, 0, .25);
		r.drawRectangle(x, y, w + border * 2, fontSize + border * 2);
		r.setColor(1, 1, 1, 1);
		r.drawText(x + border, y + border + fontSize * 0.75, text);
	}
	
	/**
	 * Open the pause menu for the game, and pause the game
	 */
	public void openPauseMenu(){
		var pauseNode = MenuNode.withAll(this.pauseMenu);
		ZusassGame.get().getPlayState().fullPause();
		this.popupMenu(pauseNode);
	}
	
	/**
	 * Close the display of the player inventory
	 */
	public void closePlayerMenus(){
		this.playerMenusOpen = false;
		this.removeMenu(this.spellListMenu);
		this.removeMenu(this.statsMenu);
	}
	
	/**
	 * Make the player inventory menu show
	 */
	public void openPlayerMenus(){
		this.playerMenusOpen = true;
		this.popupMenu(MenuNode.withAll(this.spellListMenu));
		this.popupMenu(MenuNode.withAll(this.statsMenu));
		this.statsMenu.regenerateThings();
	}
	
	/**
	 * @return true if any of the player menus are on the top, false otherwise
	 */
	public boolean anyPlayerMenuOnTop(){
		var topMenu = ZusassGame.get().getCurrentState().getTopMenu();
		return topMenu == this.getSpellListMenu() ||
			   topMenu == this.getStatsMenu();
	}
	
	/** @return See {@link #pauseMenu} */
	public PauseMenu getPauseMenu(){
		return this.pauseMenu;
	}
	
	/** @return See {@link #spellListMenu} */
	public SpellListMenu getSpellListMenu(){
		return this.spellListMenu;
	}
	
	/** @return See {@link #statsMenu} */
	public StatsMenu getStatsMenu(){
		return this.statsMenu;
	}
}
