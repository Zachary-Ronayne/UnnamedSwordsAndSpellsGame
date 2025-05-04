package tester;

import org.lwjgl.glfw.GLFW;
import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.utils.ZMath;
import zgame.core.utils.ZPoint2D;
import zgame.core.utils.ZRect2D;
import zgame.physics.collision.ZCollision;

/** A class purely for making and testing random specific bits of code */
public class DebugGame extends Game{
	
	static ZRect2D rect = new ZRect2D(300, 100, 600, 300);
	static ZPoint2D circle = new ZPoint2D(500, 300);
	static double radius = 100;
	static ZPoint2D circle2 = new ZPoint2D(800, 600);
	static double radius2 = 100;
	static boolean shift = false;
	static boolean ctrl = false;
	static boolean click = false;
	static int button = 0;
	static double lastX;
	static double lastY;
	
	static double px = 0;
	static double py = 0;
	
	static DebugGame game;
	
	public static void main(String[] args){
		game = new DebugGame();
		game.setPrintTps(false);
		game.setPrintFps(false);
		game.setPrintSoundUpdates(false);
		game.start();
	}
	
	@Override
	protected void render(Renderer r){
		super.render(r);
		r.setColor(1, 0, 0);
		r.drawRectangle(rect);
		
		if(ZMath.circleIntersectsRect(circle.x, circle.y, radius, rect.x, rect.y, rect.width, rect.height))  r.setColor(0, 1, 0, .7);
		else r.setColor(0, 0, 1, .7);
		r.drawCircle(circle.x, circle.y, radius);
		
		var c = ZCollision.rectToCircleBasic(rect.x, rect.y, rect.width, rect.height, circle.x, circle.y, radius, null);
		r.setColor(1, 1, 0, .5);
		r.drawCircle(circle.x + c.x(), circle.y + c.y(), radius);
		
		c = ZCollision.rectToRectBasic(rect.x, rect.y, rect.width, rect.height, circle.x - radius, circle.y - radius, radius * 2, radius * 2, null);
		r.setColor(0, 1, 1, .5);
		r.drawRectangle(circle.x - radius + c.x(), circle.y - radius + c.y(), radius * 2, radius * 2);
		
		r.setColor(0, 0, 1, .8);
		r.drawRectangle(px - 3, 0, 6, game.getScreenHeight());
		r.drawRectangle(0, py - 3, game.getScreenWidth(), 6);
		
		r.setColor(.5, 0,.5);
		
		var y = ZCollision.circleLineIntersection(circle.x, circle.y, radius, px, true, true);
		r.drawCircle(px, y, 6);
		y = ZCollision.circleLineIntersection(circle.x, circle.y, radius, px, true, false);
		r.drawCircle(px, y, 6);
		
		var x = ZCollision.circleLineIntersection(circle.x, circle.y, radius, py, false, true);
		r.drawCircle(x, py, 6);
		x = ZCollision.circleLineIntersection(circle.x, circle.y, radius, py, false, false);
		r.drawCircle(x, py, 6);
		
		r.setColor(0, 1, 1);
		r.drawCircle(circle2.x, circle2.y, radius2);
		c = ZCollision.circleToCircleBasic(circle2.x, circle2.y, radius2, circle.x, circle.y, radius, null);
		r.setColor(1, 1, 0, .5);
		r.drawCircle(circle.x + c.x(), circle.y + c.y(), radius);
	}
	
	@Override
	protected void keyAction(int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		DebugGame.shift = shift;
		DebugGame.ctrl = ctrl;
		double d = 20;
		if(shift){
			if(button == GLFW.GLFW_KEY_LEFT) rect.width += d;
			else if(button == GLFW.GLFW_KEY_RIGHT) rect.width -= d;
			else if(button == GLFW.GLFW_KEY_UP) rect.height += d;
			else if(button == GLFW.GLFW_KEY_DOWN) rect.height -= d;
		}
		else{
			if(button == GLFW.GLFW_KEY_LEFT) radius += d;
			else if(button == GLFW.GLFW_KEY_RIGHT) radius -= d;
			else if(button == GLFW.GLFW_KEY_UP) radius += d;
			else if(button == GLFW.GLFW_KEY_DOWN) radius -= d;
		}
	}
	
	@Override
	protected void mouseAction(int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		click = press;
		DebugGame.button = button;
	}
	
	@Override
	protected void mouseMove(double x, double y){
		if(click){
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT){
				if(shift){
					rect.x += x - lastX;
					rect.y += y - lastY;
				}
				else{
					circle.x += x - lastX;
					circle.y += y - lastY;
				}
			}
			else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT){
				if(shift) px += x - lastX;
				else py += y - lastY;
			}
		}
		lastX = x;
		lastY = y;
	}
}
