package zusass.game.things;

import zgame.things.Tag;
import zgame.things.type.GameThing;
import zgame.world.Room;
import zusass.game.LevelRoom;

/** String constants of attributes used by the Zusass game */
public enum ZusassTags implements Tag{
	/** Tells a {@link GameThing} that it can enter {@link LevelDoor}s */
	CAN_ENTER_LEVEL_DOOR,
	 /** Tells a {@link GameThing} that cannot leave a {@link LevelRoom} unless the room is cleared */
	MUST_CLEAR_LEVEL_ROOM,
	
	/** Tells a {@link Room} that it is a {@link LevelRoom} */
	IS_LEVEL,
	
}
