package zusass.game.things;

import zgame.core.GameTickable;
import zgame.core.graphics.Renderer;
import zgame.core.sound.ManagedSoundSource;
import zgame.core.state.MenuNode;
import zgame.core.utils.ZRect3D;
import zgame.physics.V3D;
import zgame.things.core.StaticThing;
import zgame.things.core.StaticThingState3D;
import zgame.things.core.GameThing;
import zgame.things.type.bounds.ModifiableRectDims3D;
import zgame.things.type.bounds.RectPrismClickable;
import zgame.world.Direction3D;
import zusass.ZusassGame;
import zusass.game.ZusassRoom;
import zusass.graphics.ZusassTexCoordsRectPrism;
import zusass.menu.spellmaker.SpellMakerMenu;
import zusass.utils.ZusassSounds;
import zusass.utils.ZusassTextureMappings;

import java.util.UUID;

/** A {@link GameThing} used as a station for the player to click on to open the spell making interface */
public class SpellMakerThing extends StaticThing<StaticThingState3D, V3D> implements ZThingClickDetector, GameTickable, RectPrismClickable, ModifiableRectDims3D{
	
	/** The uuid of this thing */
	private final String uuid;
	
	/** The menu that this thing controls */
	private final SpellMakerMenu menu;
	
	/** The texture coordinates used for the spell maker */
	private final ZusassTexCoordsRectPrism textureCoordinates;
	
	/** A source for periodically playing a sound from the spell maker */
	private final ManagedSoundSource ambientSoundSource;
	/** The amount of time, in seconds, it has been since the last ambient sound was played */
	private double timeSinceAmbient;
	/** The amount of time, in seconds, to wait before playing the next ambient sound */
	private double ambientTimeWait;
	
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
		this.textureCoordinates = new ZusassTexCoordsRectPrism(ZusassTextureMappings.SPELL_MAKER, this, 14.0 / 32.0, 7.0 / 32.0, 14.0 / 32.0, Direction3D.NORTH);
		this.ambientSoundSource = new ManagedSoundSource(ZusassSounds.MAGIC_SOUND, this, 0.9, 1.1, 0.4);
		this.timeSinceAmbient = 0;
		this.ambientTimeWait = 1;
	}
	
	@Override
	public void destroy(){
		super.destroy();
		this.menu.destroy();
		if(this.ambientSoundSource != null) this.ambientSoundSource.destroy();
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
	
	@Override
	public void tick(double dt){
		this.timeSinceAmbient += dt;
		if(this.timeSinceAmbient > this.ambientTimeWait){
			// Play the sound again after some random amount of time
			this.timeSinceAmbient = 0;
			this.ambientTimeWait = 4 + Math.random() * 2;
			this.ambientSoundSource.playSound();
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
