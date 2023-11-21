package zusass;

import com.google.gson.JsonElement;
import zgame.core.Game;
import zgame.core.file.Saveable;
import zgame.core.graphics.Renderer;
import zgame.core.utils.ZConfig;
import zgame.core.utils.ZStringUtils;
import zgame.core.window.GameWindow;
import zgame.settings.DoubleTypeSetting;
import zgame.settings.IntTypeSetting;
import zgame.settings.SettingType;
import zgame.stat.Stats;
import zusass.game.MainPlay;
import zusass.game.ZusassRoom;
import zusass.game.stat.ZusassStat;
import zusass.game.things.entities.mobs.ZusassMob;
import zusass.game.things.entities.mobs.ZusassPlayer;
import zusass.menu.mainmenu.MainMenuState;
import zusass.setting.ZusassSetting;
import zusass.setting.ZusassSettingI;
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
	
	/** The json key used to store the main chunk of data about the game */
	public final static String DATA_KEY = "data";
	/** The json key used to store the player data */
	public final static String PLAYER_KEY = "player";
	
	/** A class holding all the data used by this {@link ZusassGame} */
	private ZusassData data;
	
	/** The main player which is in this game */
	private ZusassPlayer player;
	
	/** @return See {@link #player} */
	public ZusassPlayer getPlayer(){
		return player;
	}
	
	/** @param player See player. Note that this will not account for adding the player or removing the player from a room */
	public void setPlayer(ZusassPlayer player){
		this.player = player;
	}
	
	
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
	
	/**
	 * Make a new save file for a game
	 *
	 * @param name The name of the save file
	 */
	public void createNewGame(String name){
		ZusassPlayer player = new ZusassPlayer();
		this.setPlayer(player);
		
		ZusassData data = new ZusassData();
		data.setLoadedFile(ZusassConfig.createSaveFilePath(name));
		zgame.setData(data);
		
		MainPlay play = new MainPlay(zgame);
		zgame.setCurrentState(play);
		data.checkAutoSave(zgame);
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
		this.player = Saveable.obj(PLAYER_KEY, e, ZusassPlayer.class, ZusassPlayer::new);
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
		GameWindow w = zgame.getWindow();
		if(press) return;
		
		if(button == GLFW_KEY_F9) {
			this.setPrintFps(!this.isPrintFps());
			this.setPrintTps(!this.isPrintTps());
		}
		else if(button == GLFW_KEY_F11) w.toggleFullscreen();
		else if(button == GLFW_KEY_F12) w.setUseVsync(!w.usesVsync());
		
		// TODO remove placeholder for modifying settings, implement some kind of ui
		else if(button == GLFW_KEY_F1) {
			for(var e : SettingType.nameMap.entrySet()) ZStringUtils.prints(e.getKey(), this.getAny(e.getValue())); // TODO Remove
		}
		else if(button == GLFW_KEY_F2) this.set(IntTypeSetting.TEST, this.get(IntTypeSetting.TEST) + 1, zgame.isSaveLoaded());
		else if(button == GLFW_KEY_F3) this.set(DoubleTypeSetting.TEST_D, this.get(DoubleTypeSetting.TEST_D) + .1, zgame.isSaveLoaded());
		else if(button == GLFW_KEY_F4) this.set(ZusassSettingI.Z_TEST, this.get(ZusassSettingI.Z_TEST) + 2, zgame.isSaveLoaded());
		else if(button == GLFW_KEY_F8) this.saveGlobalSettings();
	}
	
	/** Initialize the object {@link #zgame} */
	public static void init(){
		ZusassSetting.init();
		
		ZusassStat.init();
		Stats.init();
		/*
		 Init all the static stat dependencies by making a new mob, because the stats are all added when the mob is created.
		 This is kind of stupid, but whatever, it ensures they are initialized on startup
		 */
		new ZusassMob(0, 0, 0, 0){
			@Override
			protected void render(Game game, Renderer r){}
		};
		
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
	
	@Override
	public String getGlobalSettingsLocation(){
		return ZusassConfig.getGlobalSettingsPath();
	}
}
