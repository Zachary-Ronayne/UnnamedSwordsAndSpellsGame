package zusass;

import com.google.gson.JsonElement;
import zgame.core.Game;
import zgame.core.file.Saveable;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.image.ImageManager;
import zgame.core.utils.ZConfig;
import zgame.core.utils.ZFilePaths;
import zgame.core.window.GameWindow;
import zgame.core.window.WindowManager;
import zgame.settings.BooleanTypeSetting;
import zgame.stat.Stats;
import zusass.game.MainPlay;
import zusass.game.ZusassRoom;
import zusass.game.stat.ZusassStat;
import zusass.game.things.entities.mobs.ZusassMob;
import zusass.game.things.entities.mobs.ZusassPlayer;
import zusass.menu.mainmenu.MainMenuState;
import zusass.setting.ZusassSetting;
import zusass.utils.ZusassConfig;

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
	
	/** The id used for the single window of the Zusass game */
	public final static String ZUSASS_WINDOW_ID = "zusassMainWindow";
	
	/** The location used for the Zusass game assets */
	public final static String ZUSASS_ASSETS_LOCATION = "zusassets";
	
	/** The json key used to store the main chunk of data about the game */
	public final static String DATA_KEY = "data";
	/** The json key used to store the player data */
	public final static String PLAYER_KEY = "player";
	
	/** true to enable sound for the Zusass game on start up, false to disable */
	public final static boolean ENABLE_SOUND = false;
	
	/** A class holding all the data used by this {@link ZusassGame} */
	private ZusassData data;
	
	// TODO make the game manage a mapping of players to ids, allowing for multiple players
	/** The main player which is in this game */
	private ZusassPlayer player;
	
	/** @return See {@link #player} */
	public ZusassPlayer getPlayer(){
		return player;
	}
	
	/** @param player See player. Note that this will not account for adding the player or removing the player from a room */
	public void setPlayer(ZusassPlayer player){
		this.player = player;
		this.player.initSounds();
	}
	
	/** The only instance of {@link ZusassGame} which can exist */
	private static ZusassGame zgame;
	
	public static void main(String[] args){
		if(zgame != null){
			ZConfig.error("An instance of ZusassGame already exists, will not create another");
			return;
		}
		
		// Init static values
		ZFilePaths.setAssets(ZUSASS_ASSETS_LOCATION);
		ZusassSetting.init();
		ZusassStat.init();
		Stats.init();
		
		zgame = new ZusassGame();
		zgame.start();
	}
	
	/** Create the only instance of ZusassGame from this class. This constructor will place the game in the main menu */
	private ZusassGame(){
		super();
		this.getWindow().setWindowTitle("ZUSASS");
		this.getWindow().setSize(1920, 1020);
		
		// Window and performance settings
		this.setTps(100);
		this.setMaxFps(144);
		this.setInitSoundOnStart(ENABLE_SOUND);
		
		// Initialize the base data object
		this.setData(new ZusassData(0));
	}
	
	@Override
	public void init(){
		// Main game init
		super.init();
		
		// Game type setup
		this.make3D();
		this.setCurrentState(new MainMenuState());
		
		/*
		 Init all the static stat dependencies by making a new mob, because the stats are all added when the mob is created.
		 This is kind of stupid, but whatever, it ensures they are initialized on startup
		 */
		new ZusassMob(0, 0, 0, 0, 0){
			@Override
			protected void render(Renderer r){}
		};
		
		// Loading assets
		
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// Load textures
		var im = ImageManager.instance();
		im.add("brickGrayscale");
		im.add("goblin");
		im.add("zusassPlayer");
		im.add("resourceBar");
		
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// Load sounds into the game
		if(ENABLE_SOUND){
			var sm = this.getSounds();
			sm.addAllSounds();
			sm.setDistanceScalar(10);
			sm.getEffectsPlayer().setPaused(false);
			sm.getEffectsPlayer().setMuted(false);
		}
	}
	
	/**
	 * Make a new save file for a game
	 *
	 * @param name The name of the save file
	 * @param seed The seed for the new game
	 */
	public void createNewGame(String name, long seed){
		ZusassPlayer player = new ZusassPlayer();
		this.setPlayer(player);
		
		ZusassData data = new ZusassData(seed);
		data.setLoadedFile(ZusassConfig.createSaveFilePath(name));
		zgame.setData(data);
		
		MainPlay play = new MainPlay();
		zgame.setCurrentState(play);
		data.checkAutoSave();
	}
	
	@Override
	public boolean save(JsonElement e){
		Saveable.save(DATA_KEY, e, this.getData());
		Saveable.save(PLAYER_KEY, e, this.getPlayer());
		return true;
	}
	
	@Override
	public boolean load(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		this.data = Saveable.obj(DATA_KEY, e, ZusassData.class, ZusassData::new);
		this.setPlayer(Saveable.obj(PLAYER_KEY, e, ZusassPlayer.class, ZusassPlayer::new));
		return true;
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
		if(press) return;
		
		if(button == GLFW_KEY_F9){
			this.toggle(BooleanTypeSetting.PRINT_FPS, true);
			this.toggle(BooleanTypeSetting.PRINT_TPS, true);
		}
		else if(button == GLFW_KEY_F11) zgame.toggleFullscreen();
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
	
	@Override
	public String getGlobalSettingsLocation(){
		return ZusassConfig.getGlobalSettingsPath();
	}
	
	@Override
	public void onWindowSizeChange(int newW, int newH){
		super.onWindowSizeChange(newW, newH);
		
		// If the game is not in the play state, set menu size to the window size
		var currentState = this.getCurrentState();
		if(this.getPlayState() != currentState){
			var menu = currentState.getMenu();
			if(menu != null){
				menu.setWidth(newW);
				menu.setHeight(newH);
			}
		}
	}
	
	@Override
	public String getGameWindowId(){
		return ZUSASS_WINDOW_ID;
	}
	
	/** @return The global instance of the Zusass game */
	public static ZusassGame get(){
		return zgame;
	}
	
	/** @return The single window used by the game */
	public static GameWindow window(){
		return WindowManager.get().getWindow(ZUSASS_WINDOW_ID);
	}
	
}
