package zusass.game.things;

import zgame.core.Game;
import zgame.core.GameTickable;
import zgame.core.graphics.Renderer;
import zgame.core.state.MenuNode;
import zgame.things.still.StaticThing3D;
import zgame.things.type.GameThing;
import zusass.ZusassGame;
import zusass.menu.spellmaker.SpellMakerMenu;

import java.util.UUID;

/** A {@link GameThing} used as a station for the player to click on to open the spell making interface */
public class SpellMakerThing extends StaticThing3D implements ZThingClickDetector, GameTickable{
	
	/** The uuid of this thing */
	private final String uuid;
	
	/** The menu that this thing controls */
	private final SpellMakerMenu menu;
	
	/**
	 * Make a spell maker at the given position
	 * @param zgame The game which this thing will exist in
	 * @param x The upper left hand x coordinate
	 * @param y The upper left hand y coordinate
	 */
	public SpellMakerThing(ZusassGame zgame, double x, double y, double z){
		super(x, y, z, 0.4, 0.2, 0.4);
		this.uuid = UUID.randomUUID().toString();
		
		this.menu = new SpellMakerMenu(zgame);
	}
	
	@Override
	public void destroy(){
		super.destroy();
		this.menu.destroy();
	}
	
	@Override
	protected void render(Game game, Renderer r){
		// TODO figure out rendering
//		var b = this.getBounds();
//		r.setColor(.6, 0, .8);
//		r.drawRectangle(b);
//		r.setColor(.1, 0, .1);
//		r.setFontSize(32);
//		r.drawText(b.getX() + 4, b.getY() + 40, "SPELLS");
	}
	
	@Override
	public int getRenderPriority(){
		return -100;
	}
	
	@Override
	public boolean handleZusassPress(ZusassGame zgame){
		var c = zgame.getCurrentState();
		// Don't pop up this menu if it is already showing this menu
		if(c.showingMenu(menu)) return false;
		
		c.popupMenu(zgame, MenuNode.withAll(this.menu));
		return true;
	}
	
	@Override
	public void tick(Game game, double dt){
		// TODO figure out how to handle intersections
//		var zgame = (ZusassGame)game;
//		var p = zgame.getPlayer();
//		var play = zgame.getPlayState();
//		if(play.showingMenu(this.menu) && !p.getBounds().intersects(this.getBounds())){
//			zgame.onNextLoop(() -> play.removeMenu(game, this.menu));
//		}
	}
	
	/** @return See {@link #uuid} */
	@Override
	public String getUuid(){
		return this.uuid;
	}
}
