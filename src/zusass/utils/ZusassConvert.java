package zusass.utils;

import zgame.things.entity.MobThing;
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
		MobThing m = thing.asMob();
		if(m == null) return null;
		ZusassPlayer p = (ZusassPlayer)m;
		return p.asPlayer();
	}
	
	/**
	 * @param thing A thing to get as an Npc
	 * @return The Npc, or null if it could not be converted to an Npc
	 */
	public static Npc toNpc(GameThing thing){
		MobThing m = thing.asMob();
		if(m == null) return null;
		ZusassMob zm = (ZusassMob)m;
		return zm.asNpc();
	}

	/** Cannot instantiate {@link ZusassConvert} */
	private ZusassConvert(){
	}
	
}
