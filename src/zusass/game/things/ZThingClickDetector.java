package zusass.game.things;

import zgame.core.Game;
import zgame.core.utils.ZRect3D;
import zgame.things.ThingClickDetector2D;
import zgame.things.ThingClickDetector3D;
import zgame.things.type.bounds.Bounds3D;
import zusass.ZusassGame;

/** A {@link ThingClickDetector2D} used for the Zusass game */
public interface ZThingClickDetector extends ThingClickDetector3D, Bounds3D{
	
	@Override
	default ZRect3D getThingBounds(){
		return this.getBounds();
	}
	
	@Override
	default ZRect3D getPlayerBounds(Game game){
		var zgame = (ZusassGame)game;
		var player = zgame.getPlayer();
		if(player == null) return null;
		return player.getBounds();
	}
	
	@Override
	default boolean handlePress(Game game){
		var zgame = (ZusassGame)game;
		if(!ThingClickDetector3D.super.handlePress(zgame)) return false;
		return this.handleZusassPress(zgame);
	}
	
	/**
	 * Called when this thing in the Zusass game is clicked
	 * @param zgame The game where the click happened
	 * @return true if this was activated, false otherwise
	 */
	boolean handleZusassPress(ZusassGame zgame);
}
