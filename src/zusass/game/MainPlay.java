package zusass.game;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.graphics.font.TextBuffer;
import zgame.core.state.MenuNode;
import zgame.core.state.PlayState;
import zusass.ZusassGame;
import zusass.game.things.entities.mobs.ZusassPlayer;
import zusass.menu.mainmenu.MainMenuState;
import zusass.menu.pause.PauseMenu;

import static org.lwjgl.glfw.GLFW.*;

/**
 * The main {@link PlayState} used by the Zusass game
 * I initially called this ZusassPlay, but I um... changed it
 */
public class MainPlay extends PlayState{
	
	/** A text buffer holding if the player is casting or attacking */
	private final TextBuffer castAttackTextBuffer;
	
	/**
	 * Initialize the main play state for the Zusass game
	 *
	 * @param zgame The {@link Game} using this state
	 */
	public MainPlay(ZusassGame zgame){
		this.enterHub(zgame);
		this.castAttackTextBuffer = new TextBuffer(200, 200);
		this.castAttackTextBuffer.setFont(zgame.getDefaultFont().size(30));
		this.castAttackTextBuffer.setTextX(3);
		this.castAttackTextBuffer.setTextY(22);
		this.castAttackTextBuffer.setText(" ");
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
		ZusassPlayer player = new ZusassPlayer();
		player.setX(20);
		player.setY(hub.maxY() - player.getHeight());
		player.setLockCamera(true);
		hub.addThing(player);
		player.centerCamera(zgame);
		hub.setPlayer(player);
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
		
		// Draw a basic health bar
		ZusassPlayer p = this.getCurrentRoom().getPlayer();
		if(p == null) return;
		r.setColor(.5, .5, .5);
		r.drawRectangle(10, 10, 200, 20);
		r.setColor(1, 0, 0);
		r.drawRectangle(10, 10, 200 * p.currentHealthPerc(), 20);
		
		// Draw a stamina bar
		r.setColor(.5, .5, .5);
		r.drawRectangle(10, 35, 200, 20);
		r.setColor(0, 1, 0);
		r.drawRectangle(10, 35, 200 * p.currentStaminaPerc(), 20);
		
		// Draw a mana bar
		r.setColor(.5, .5, .5);
		r.drawRectangle(10, 60, 200, 20);
		r.setColor(0, 0, 1);
		r.drawRectangle(10, 60, 200 * p.currentManaPerc(), 20);
		
		// Draw if the player is casting or attacking
		r.setColor(1, 1, 1, 1);

		var text = p.isCasting() ? "Spell Mode" : "Attack Mode";
		// Kind of weird way to update this, but whatever, this is just temporary
		if(this.castAttackTextBuffer.getText().charAt(0) != text.charAt(0)) this.castAttackTextBuffer.setText(text);
		this.castAttackTextBuffer.drawToRenderer(10, 85, r);
	}
	
}
