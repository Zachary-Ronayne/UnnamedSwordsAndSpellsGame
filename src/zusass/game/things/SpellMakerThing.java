package zusass.game.things;

import zgame.core.Game;
import zgame.core.GameTickable;
import zgame.core.graphics.Renderer;
import zgame.core.sound.SoundManager;
import zgame.core.sound.SoundSource;
import zgame.core.state.MenuNode;
import zgame.core.utils.ZRect3D;
import zgame.things.still.StaticThing3D;
import zgame.things.type.GameThing;
import zgame.things.type.bounds.ModifiableRectDims3D;
import zgame.things.type.bounds.RectPrismClickable;
import zgame.world.Direction3D;
import zusass.ZusassGame;
import zusass.game.ZusassRoom;
import zusass.graphics.ZusassTexCoordsRectPrism;
import zusass.menu.spellmaker.SpellMakerMenu;

import java.util.UUID;

/** A {@link GameThing} used as a station for the player to click on to open the spell making interface */
public class SpellMakerThing extends StaticThing3D implements ZThingClickDetector, GameTickable, RectPrismClickable, ModifiableRectDims3D{
	
	/** The uuid of this thing */
	private final String uuid;
	
	/** The menu that this thing controls */
	private final SpellMakerMenu menu;
	
	/** The texture coordinates used for the spell maker */
	private final ZusassTexCoordsRectPrism textureCoordinates;
	
	// TODO implement properly
	private SoundSource sound;
	
	/**
	 * Make a spell maker at the given position
	 *
	 * @param x The upper left hand x coordinate
	 * @param y The upper left hand y coordinate
	 */
	public SpellMakerThing(double x, double y, double z){
		super(x, y, z, 1, 1, 1);
		this.uuid = UUID.randomUUID().toString();
		
		this.menu = new SpellMakerMenu();
		this.textureCoordinates = new ZusassTexCoordsRectPrism("spellMaker", this, 14.0 / 32.0, 7.0 / 32.0, 14.0 / 32.0, Direction3D.NORTH);
		this.sound = SoundManager.get().createSource(x, y, z);
	}
	
	@Override
	public void destroy(){
		super.destroy();
		this.menu.destroy();
	}
	
	@Override
	public void render(Renderer r){
		this.textureCoordinates.renderSelectable(r,this);
	}
	
	@Override
	public void handleZusassPress(ZusassRoom room){
		var c = ZusassGame.get().getCurrentState();
		// Don't pop up this menu if it is already showing this menu
		if(c.showingMenu(menu)) return;
		
		c.popupMenu(MenuNode.withAll(this.menu));
	}
	
	// TODO implement properly
	private double timer = 0;
	
	@Override
	public void tick(double dt){
		timer += dt;
		if(timer > 1){
			timer = 0;
			// TODO maybe keep this test sound, just as a passive background sound
//			Game.get().playEffect(this.sound, "win");
		}
		
		
		
		var zgame = ZusassGame.get();
		var p = zgame.getPlayer();
		var play = zgame.getPlayState();
		// If the player is too far away from the spell maker, then make them leave the menu
		if(play.showingMenu(this.menu) && p.distance(this) > p.getClickRange() + this.getWidth()){
			zgame.onNextLoop(() -> play.removeMenu(this.menu));
		}
	}
	
	/** @return See {@link #uuid} */
	@Override
	public String getUuid(){
		return this.uuid;
	}
	
	@Override
	public ZRect3D getBounds(){
		return super.getBounds();
	}
}
