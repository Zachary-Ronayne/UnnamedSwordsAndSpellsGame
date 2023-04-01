package zusass.game.things;

import zgame.things.Tag;
import zgame.things.type.GameThing;
import zgame.world.Room;
import zusass.game.Hub;
import zusass.game.LevelRoom;
import zusass.game.things.entities.mobs.ZusassMob;

/** String constants of attributes used by the Zusass game */
public enum ZusassTags implements Tag{
	/** Tells a {@link GameThing} that it can enter {@link LevelDoor}s */
	CAN_ENTER_LEVEL_DOOR,
	/** Tells a {@link GameThing} that cannot leave a {@link LevelRoom} unless the room is cleared */
	MUST_CLEAR_LEVEL_ROOM,
	/** Tells a {@link ZusassMob} to restore all resources, i.e. health, stamina, and mana, to full when they enter the {@link Hub} */
	HUB_ENTER_RESTORE,
	
	/** Tells a {@link Room} that it is a {@link LevelRoom} */
	IS_LEVEL,
	
}
