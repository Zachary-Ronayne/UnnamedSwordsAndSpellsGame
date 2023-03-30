package zusass.game;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.state.MenuNode;
import zgame.core.state.PlayState;
import zgame.stat.StatType;
import zusass.ZusassGame;
import zusass.game.stat.ZusassStat;
import zusass.game.things.entities.mobs.ZusassPlayer;
import zusass.menu.mainmenu.MainMenuState;
import zusass.menu.pause.PauseMenu;

import static org.lwjgl.glfw.GLFW.*;

/**
 * The main {@link PlayState} used by the Zusass game
 * I initially called this ZusassPlay, but I um... changed it
 */
public class MainPlay extends PlayState{
	
	/**
	 * Initialize the main play state for the Zusass game
	 *
	 * @param zgame The {@link Game} using this state
	 */
	public MainPlay(ZusassGame zgame){
		this.enterHub(zgame);
	}
	
	/**
	 * Set the current room of the game to the main hub
	 *
	 * @param zgame The {@link Game} using this state
	 */
	public void enterHub(ZusassGame zgame){
		// Make the hub
		Hub hub = new Hub(zgame);
		
		// Place the player
		ZusassPlayer player = zgame.getPlayer();
		player.setX(20);
		player.setY(hub.maxY() - player.getHeight());
		player.setLockCamera(true);
		hub.addThing(player);
		player.centerCamera(zgame);
		this.setCurrentRoom(hub);
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
		
		// On releasing escape, enter the pause menu
		if(button == GLFW_KEY_ESCAPE){
			ZusassGame zgame = (ZusassGame)game;
			MenuNode pauseNode = MenuNode.withAll(new PauseMenu(zgame));
			zgame.getPlayState().fullPause();
			this.popupMenu(pauseNode);
		}
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
		super.renderHud(game, r);
		var zgame = (ZusassGame)game;
		
		// Draw a basic health bar
		ZusassPlayer p = zgame.getPlayer();
		if(p == null) return;
		this.drawResourceBar(r, p, ZusassStat.HEALTH, ZusassStat.HEALTH_MAX, 0, new ZColor(1, 0, 0), new ZColor(1));
		this.drawResourceBar(r, p, ZusassStat.STAMINA, ZusassStat.STAMINA_MAX, 1, new ZColor(0, 1, 0), new ZColor(.2));
		this.drawResourceBar(r, p, ZusassStat.MANA, ZusassStat.MANA_MAX, 2, new ZColor(0, 0, 1), new ZColor(1));
		
		// Draw if the player is casting or attacking
		r.setColor(1, 1, 1, 1);
		// Using draw text like this is inefficient, but whatever, this is temp code
		r.drawText(10, 100, p.isCasting() ? "Spell Mode" : "Attack Mode");
	}
	
	/** Temporary code for simplicity of testing */
	private void drawResourceBar(Renderer r, ZusassPlayer p, StatType current, StatType max, int index, ZColor color, ZColor textColor){
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
}
