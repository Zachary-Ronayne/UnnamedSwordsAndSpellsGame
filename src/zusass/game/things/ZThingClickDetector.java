package zusass.game.things;

import zgame.core.Game;
import zgame.core.utils.ZRect;
import zgame.things.ThingClickDetector;
import zgame.things.type.Bounds;
import zusass.ZusassGame;

/** A {@link ThingClickDetector} used for the Zusass game */
public interface ZThingClickDetector extends ThingClickDetector, Bounds{
	
	@Override
	default ZRect getThingBounds(){
		return this.getBounds();
	}
	
	@Override
	default ZRect getPlayerBounds(Game game){
		var zgame = (ZusassGame)game;
		var player = zgame.getPlayer();
		if(player == null) return null;
		return player.getBounds();
	}
	
	@Override
	default boolean handlePress(Game game){
		var zgame = (ZusassGame)game;
		if(!ThingClickDetector.super.handlePress(zgame)) return false;
		return handleZPress(zgame);
	}
	
	/**
	 * Called when this thing in the Zusass game is clicked
	 * @param zgame The game where the click happened
	 * @return true if this was activated, false otherwise
	 */
	boolean handleZPress(ZusassGame zgame);
}
