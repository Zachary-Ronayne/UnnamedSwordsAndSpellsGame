package zusass.game;

import zgame.core.utils.NotNullList;
import zgame.things.core.GameThing;
import zgame.things.core.Room;
import zgame.things.core.Room3D;
import zusass.ZusassGame;
import zusass.game.things.ZThingClickDetector;
import zusass.game.things.ZusassTags;
import zusass.game.things.entities.mobs.ZusassMob;

/** A {@link Room} used by the Zusass game */
public class ZusassRoom extends Room3D{
	
	/** The name of a song to play while the player is in this room, background music, null for no music */
	private String songName;
	
	/** Create a new room with nothing in it */
	public ZusassRoom(){
		this(0, 0, 0);
	}
	
	/**
	 * Create a new room of the given size
	 *
	 * @param xTiles The number of tiles on the x axis
	 * @param yTiles The number of tiles on the y axis
	 * @param zTiles The number of tiles on the z axis
	 */
	public ZusassRoom(int xTiles, int yTiles, int zTiles){
		super(xTiles, yTiles, zTiles);
		this.getAllThings().addClass(ZusassMob.class);
		this.getAllThings().addClass(ZThingClickDetector.class);
	}
	
	/** @return All the mobs which are in this {@link ZusassRoom} */
	public NotNullList<ZusassMob> getMobs(){
		return this.getAllThings().get(ZusassMob.class);
	}
	
	/** @return See {@link #songName} */
	public String getSongName(){
		return this.songName;
	}
	
	/** @param songName See {@link #songName} */
	public void setSongName(String songName){
		this.songName = songName;
	}
	
	/** Update the state of the song that is being played in the room */
	private void updateSong(){
		var sm = ZusassGame.get().getSounds();
		var music = sm.getMusicPlayer();
		if(this.songName == null) music.stopSounds();
		else{
			sm.playMusic(this.getSongName());
		}
	}
	
	@Override
	public void addThing(GameThing thing){
		super.addThing(thing);
		if(thing.hasTag(ZusassTags.USE_ROOM_MUSIC)) this.updateSong();
	}
}
