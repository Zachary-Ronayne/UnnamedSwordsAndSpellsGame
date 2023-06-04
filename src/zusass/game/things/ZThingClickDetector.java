package zusass.game.things;

import zgame.core.Game;
import zgame.core.utils.ZRect;
import zgame.things.ThingClickDetector;
import zusass.ZusassGame;

/** A {@link ThingClickDetector} used for the Zusass game */
public interface ZThingClickDetector extends ThingClickDetector{
	
	@Override
	default ZRect getPlayerBounds(Game game){
		var zgame = (ZusassGame)game;
		var player = zgame.getPlayer();
		if(player == null) return null;
		return player.getBounds();
	}
	
}
