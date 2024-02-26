package tester;

import org.lwjgl.glfw.GLFW;
import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.graphics.buffer.DrawableBuffer;
import zgame.core.graphics.buffer.DrawableGameBuffer;
import zgame.core.graphics.font.TextBuffer;
import zgame.core.state.GameState;
import zgame.core.state.MenuState;
import zgame.core.utils.ZRect2D;
import zgame.menu.Menu;
import zgame.menu.MenuText;
import zgame.menu.MenuThing;

/** A demo tester class for debugging buffers, mainly trying to fix buffers being blank for 1 frame after redrawing */
public class BufferDemo extends Game{
	
	public static BufferTester buffer;
	public static TextBuffer textBuffer;
	public static Game game;
	public static GameState state;
	
	public static void main(String[] args){
		game = new Game();
		game.setPrintFps(false);
		game.setPrintTps(false);
		game.setMaxFps(100);
		game.setTps(100);
		game.getWindow().setUseVsync(false);
		game.getWindow().center();
		buffer = new BufferTester(500, 300);
		
		var menu = new Menu(){
			@Override
			public void render(Game game, Renderer r, ZRect2D bounds){
				r.setColor(new ZColor(.5));
				r.fill();
				super.render(game, r, bounds);
			}
		};
		state = new MenuState(game, menu){};
		var thing = new MenuText(100, 100, 500, 300, game){
			@Override
			public void keyActionFocused(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
				super.keyActionFocused(game, button, press, shift, alt, ctrl);
				if(!press && button == GLFW.GLFW_KEY_SPACE){
					this.getTextBuffer().updateRedraw(true);
				}

				if(!press && button == GLFW.GLFW_KEY_F11) game.toggleFullscreen();
			}

			@Override
			public void render(Game game, Renderer r, ZRect2D bounds){
				super.render(game, r, bounds);
				r.setColor(1, 0, 0);
				r.drawRectangle(new ZRect2D(bounds.x + 10, bounds.y + 10, 100, 100));
			}
		};
		thing.setText("test123");
		thing.setFontSize(100);
		thing.setFontColor(new ZColor(.5, 0, .5));
		thing.setFill(new ZColor(.8, .8, 1));
		thing.centerText();
		menu.addThing(thing);
		
		MenuThing t = new MenuThing(){
			@Override
			public void render(Game game, Renderer r, ZRect2D bounds){
				super.render(game, r, bounds);
				r.setColor(0, 1, 0, .5);
				r.drawRectangle(bounds.x - 20, bounds.y - 20, bounds.width + 80, bounds.height + 80);
			}
		};
		t.setRelX(700);
		t.setRelY(50);
		t.setWidth(100);
		t.setHeight(300);
		menu.addThing(t);
		
		t = new MenuThing(){
			@Override
			public void render(Game game, Renderer r, ZRect2D bounds){
				super.render(game, r, bounds);
				r.setColor(0, 1, 1, .5);
				r.fill();
			}
		};
		t.setRelX(850);
		t.setRelY(50);
		t.setWidth(100);
		t.setHeight(300);
		t.setDefaultUseBuffer(true);
		menu.addThing(t);
		
		t = new MenuThing(){
			@Override
			public void render(Game game, Renderer r, ZRect2D bounds){
				super.render(game, r, bounds);
				r.setColor(1, 0, 1, .5);
				r.fill();
			}
		};
		t.setRelX(1000);
		t.setRelY(50);
		t.setWidth(100);
		t.setHeight(300);
		t.setLimitToBounds(true);
		menu.addThing(t);

		game.setCurrentState(state);
		game.start();
	}
	
	public static class BufferTester extends DrawableGameBuffer{
		/**
		 * Create a {@link DrawableBuffer} of the given size.
		 *
		 * @param width See {@link #getWidth()}
		 * @param height See {@link #getHeight()}
		 */
		public BufferTester(int width, int height){
			super(width, height);
		}
		
		@Override
		public void draw(Game game, Renderer r){
			super.draw(game, r);
			r.setColor(new ZColor(1, 0, 0));
			r.fill();
		}
	}
}
