package zusass.game.things;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.state.MenuNode;
import zgame.things.type.GameThing;
import zgame.things.type.PositionedRectangleThing;
import zusass.ZusassGame;
import zusass.menu.spellmaker.SpellMakerMenu;

/** A {@link GameThing} used as a station for the player to click on to open the spell making interface */
public class SpellMakerThing extends PositionedRectangleThing implements ZThingClickDetector{
	
	public SpellMakerThing(double x, double y){
		super(x, y);
		this.setWidth(130);
		this.setHeight(70);
	}
	
	@Override
	protected void render(Game game, Renderer r){
		var b = this.getBounds();
		r.setColor(.6, 0, .8);
		r.drawRectangle(b);
		r.setColor(.1, 0, .1);
		r.setFontSize(32);
		r.drawText(b.getX() + 4, b.getY() + 40, "SPELLS");
	}
	
	@Override
	public int getRenderPriority(){
		return -100;
	}
	
	@Override
	public boolean handleZPress(ZusassGame zgame){
		var c = zgame.getCurrentState();
		// Don't pop up this menu if there is already a menu open
		if(c.getStackSize() != 0) return false;
		
		c.popupMenu(MenuNode.withAll(new SpellMakerMenu(zgame)));
		return true;
	}
}
