package tester;

import org.lwjgl.glfw.GLFW;
import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.utils.ZMath;
import zgame.core.utils.ZPoint;
import zgame.core.utils.ZRect;

/** A class purely for making and testing random specific bits of code */
public class DebugGame extends Game{
	
	static ZRect rect = new ZRect(300, 100, 600, 300);
	static ZPoint circle = new ZPoint(500, 300);
	static double radius = 100;
	static boolean shift = false;
	static boolean click = false;
	static double lastX;
	static double lastY;
	
	public static void main(String[] args){
		var g = new DebugGame();
		g.setPrintTps(false);
		g.setPrintFps(false);
		g.setPrintSoundUpdates(false);
		g.getWindow().center();
		g.start();
	}
	
	@Override
	protected void render(Renderer r){
		super.render(r);
		r.setColor(1, 0, 0);
		r.drawRectangle(rect);
		
		if(ZMath.circleIntersectsRect(circle.x, circle.y, radius, rect.x, rect.y, rect.width, rect.height))  r.setColor(0, 1, 0, .7);
		else r.setColor(0, 0, 1, .7);
		r.drawCircle(circle.x, circle.y, radius);
	}
	
	@Override
	protected void keyAction(int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		DebugGame.shift = shift;
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
	}
	
	@Override
	protected void mouseMove(double x, double y){
		if(click){
			if(shift){
				rect.x += x - lastX;
				rect.y += y - lastY;
			}
			else{
				circle.x += x - lastX;
				circle.y += y - lastY;
			}
		}
		lastX = x;
		lastY = y;
	}
}
