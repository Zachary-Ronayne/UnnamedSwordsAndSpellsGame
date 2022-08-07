package zusass;

import zgame.core.Game;
import zgame.core.window.GameWindow;
import zusass.game.MainPlay;
import zusass.menu.mainmenu.MainMenuState;

import static org.lwjgl.glfw.GLFW.*;

import com.google.gson.JsonObject;

/**
 * The main class for the ZUSASS Game.
 * ZUSASS is an acronym
 * Zac's
 * Untitled
 * Swords
 * and
 * Spells
 * Sandbox
 */
public class ZUSASSGame extends Game<ZUSASSData>{

	/** The json key used to store the main chunk of data about the game */
	public final static String DATA_KEY = "data";
	
	/** Create the only instance of ZUSASSGame from this class. This constructor will place the game in the main menu */
	private ZUSASSGame(){
		super();
		// Window and performance settings
		this.setTps(100);
		this.setMaxFps(0);
		this.setCurrentState(new MainMenuState(this));
		GameWindow w = this.getWindow();
		w.setUseVsync(true);
		w.center();
		
		// Loading assets
		this.getFonts().addAll();

		// Initialize the base data object
		this.setData(new ZUSASSData());
		
		// Play state
		this.setPlayState(new MainPlay(this));
	}
	
	/** The only instance of {@link ZUSASSGame} which can exist */
	private static ZUSASSGame game = null;
	
	public static void main(String[] args){
		init();
		game.start();
	}

	@Override
	public JsonObject save(JsonObject obj){
		obj.add(DATA_KEY, this.getData().save());
		return obj;
	}

	@Override
	public JsonObject load(JsonObject obj) throws ClassCastException, IllegalStateException{
		this.getData().load(DATA_KEY, obj);
		return obj;
	}
	
	@Override
	protected void keyAction(int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.keyAction(button, press, shift, alt, ctrl);
		GameWindow w = game.getWindow();
		if(button == GLFW_KEY_F11 && !press) w.toggleFullscreen();
		else if(button == GLFW_KEY_F12 && !press) w.setUseVsync(!w.usesVsync());
	}
	
	/** Initialize the object {@link #game} */
	public static void init(){
		if(game != null) return;
		game = new ZUSASSGame();
	}
}
