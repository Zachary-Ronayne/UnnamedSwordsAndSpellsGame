package zusass.utils;

import zgame.things.type.GameThing;
import zusass.game.things.entities.mobs.Npc;
import zusass.game.things.entities.mobs.ZusassMob;
import zusass.game.things.entities.mobs.ZusassPlayer;

/** Utility methods for converting objects used by the Zusass game */
public final class ZusassConvert{
	
	/**
	 * @param thing A thing to get as a ZusassPlayer
	 * @return The ZusassPlayer, or null if it could not be converted to a ZusassPlayer
	 */
	public static ZusassPlayer toPlayer(GameThing thing){
		// TODO remove instanceof
		if(thing instanceof ZusassPlayer z) return z;
		return null;
	}
	
	/**
	 * @param thing A thing to get as an Npc
	 * @return The Npc, or null if it could not be converted to an Npc
	 */
	public static Npc toNpc(GameThing thing){
		// TODO remove instanceof
		if(thing instanceof Npc n) return n;
		return null;
	}
	
	/** Cannot instantiate {@link ZusassConvert} */
	private ZusassConvert(){
	}
	
}
