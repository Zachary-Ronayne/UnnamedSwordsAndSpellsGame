package zusass;

import com.google.gson.JsonElement;
import zgame.core.Game;
import zgame.core.utils.ZConfig;
import zgame.core.window.GameWindow;
import zusass.game.MainPlay;
import zusass.game.ZusassRoom;
import zusass.game.stat.ZusassStat;
import zusass.menu.mainmenu.MainMenuState;

import static org.lwjgl.glfw.GLFW.*;

import java.io.File;

/**
 * The main class for the Zusass Game.
 * Zusass is an acronym
 * Zac's
 * Untitled
 * Swords
 * and
 * Spells
 * Sandbox
 */
public class ZusassGame extends Game{
	
	/** The json key used to store the main chunk of data about the game */
	public final static String DATA_KEY = "data";
	/** The json key used to store the player data */
	public final static String PLAYER_KEY = "player";
	
	/** A class holding all the data used by this {@link ZusassGame} */
	private ZusassData data;
	
	/*
	 * issue#16 make the infinite levels have the same seed for each level based on the save's seed. You can input a seed when you make the save, or randomly generate one
	 * The seed for each level is randomly generated by the level number and the save's seed
	 *
	 * Get rid of the stupid type parameter for game and just rely on casting for game specific components
	 */
	
	/** Create the only instance of ZusassGame from this class. This constructor will place the game in the main menu */
	private ZusassGame(){
		super();
		// Window and performance settings
		this.setTps(100);
		this.setMaxFps(100);
		this.setCurrentState(new MainMenuState(this));
		GameWindow w = this.getWindow();
		w.setUseVsync(true);
		w.center();
		
		// Loading assets
		this.getFonts().addAll();
		
		// Initialize the base data object
		this.setData(new ZusassData());
	}
	
	/** The only instance of {@link ZusassGame} which can exist */
	private static ZusassGame zgame = null;
	
	public static void main(String[] args){
		init();
		zgame.start();
	}
	
	@Override
	public JsonElement save(JsonElement e){
		var obj = e.getAsJsonObject();
		obj.add(DATA_KEY, this.getData().save());
		obj.add(PLAYER_KEY, this.getCurrentRoom().getPlayer().save());
		return obj;
	}
	
	@Override
	public JsonElement load(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		this.getData().load(DATA_KEY, e);
		var room = this.getCurrentRoom();
		if(room != null) room.getPlayer().load(PLAYER_KEY, e);
		return e;
	}
	
	@Override
	public boolean loadGame(String path){
		boolean success = super.loadGame(path);
		if(success) this.getData().setLoadedFile(path);
		return success;
	}
	
	@Override
	public boolean saveGame(String path){
		// If the path doesn't already exist, create it
		File file = new File(path);
		File directory = file.getParentFile();
		if(!directory.exists()){
			try{
				directory.mkdirs();
			}catch(SecurityException e){
				ZConfig.error(e, "Couldn't make directories. Failed to save file at path:", path);
			}
		}
		return super.saveGame(path);
	}
	
	/**
	 * Save the currently loaded game to its save file. Does nothing if no file is loaded
	 *
	 * @return true if the save was successful, false otherwise
	 */
	public boolean saveLoadedGame(){
		String path = this.getData().getLoadedFile();
		if(path == null) return false;
		return this.saveGame(path);
	}
	
	@Override
	protected void keyAction(int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.keyAction(button, press, shift, alt, ctrl);
		GameWindow w = zgame.getWindow();
		if(press) return;
		
		if(button == GLFW_KEY_F9) {
			this.setPrintFps(!this.isPrintFps());
			this.setPrintTps(!this.isPrintTps());
		}
		else if(button == GLFW_KEY_F11) w.toggleFullscreen();
		else if(button == GLFW_KEY_F12) w.setUseVsync(!w.usesVsync());
	}
	
	/** Initialize the object {@link #zgame} */
	public static void init(){
		ZusassStat.init();
		if(zgame != null) return;
		zgame = new ZusassGame();
	}
	
	/** @return See {@link #data} */
	public ZusassData getData(){
		return this.data;
	}
	
	/** @param data See {@link #data} */
	public void setData(ZusassData data){
		this.data = data;
	}
	
	@Override
	public MainPlay getPlayState(){
		return (MainPlay)super.getPlayState();
	}
	
	@Override
	public ZusassRoom getCurrentRoom(){
		return (ZusassRoom)(super.getCurrentRoom());
	}
	
}
