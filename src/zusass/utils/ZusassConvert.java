package zusass.utils;

import zgame.things.type.GameThing;
import zusass.game.things.entities.mobs.ZusassPlayer;

/** Utility methods for converting objects used by the Zusass game */
public final class ZusassConvert{
	
	/**
	 * @param thing A thing to get as a ZusassPlayer
	 * @return The ZusassPlayer, or null if it could not be converted to a ZusassPlayer
	 */
	public static ZusassPlayer toPlayer(GameThing thing){
		if(thing instanceof ZusassPlayer z) return z;
		return null;
	}
	
	/** Cannot instantiate {@link ZusassConvert} */
	private ZusassConvert(){
	}
	
}
