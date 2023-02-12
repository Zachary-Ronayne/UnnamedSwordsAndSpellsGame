package zusass.game.things;

import zgame.things.Tag;
import zgame.things.type.GameThing;
import zgame.world.Room;
import zusass.game.LevelRoom;
import zusass.game.things.entities.mobs.ZusassPlayer;

/** String constants of attributes used by the Zusass game */
public enum ZusassTags implements Tag{
	/** Tells a {@link GameThing} that it can enter {@link LevelDoor}s */
	CAN_ENTER_LEVEL_DOOR,
	 /** Tells a {@link GameThing} that it is a {@link ZusassPlayer} */
	IS_PLAYER,
	
	/** Tells a {@link Room} that it is a {@link LevelRoom} */
	IS_LEVEL,
	
}
