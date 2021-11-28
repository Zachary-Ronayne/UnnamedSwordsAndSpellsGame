package zgametest.graphics;

import static org.junit.jupiter.api.Assertions.*;
import static zgametest.Tester.DELTA;

import org.junit.jupiter.api.*;

import zgame.Game;
import zgame.graphics.Renderer;
import zgame.window.GameWindow;
import zgametest.TestGame;

public class RendererTest{
	
	private static Game game;
	private static GameWindow window;
	private static Renderer renderer;
	
	private double coordinateValue;
	
	/** Run one time before all tests occur, use for initializtion of static values */
	@BeforeAll
	public static void init(){
		game = new TestGame();
		renderer = game.getWindow().getRenderer();
	}
	
	/** Run one time before each test occurs, use for initializtion of values that must be the same before each test */
	@BeforeEach
	public void setup(){
		window.setSize(400, 150);
		coordinateValue = 5;
	}
	
	@Test
	public void testResize(){
	}
	
	@Test
	public void testClear(){
	}
	
	@Test
	public void testRenderModeShapes(){
	}
	
	@Test
	public void testRenderModeImage(){
	}
	
	@Test
	public void testRenderModeBuffer(){
	}
	
	@Test
	public void testSetLoadedShader(){
	}
	
	@Test
	public void testDrawToRenderer(){
	}
	
	@Test
	public void testDrawToWindow(){
	}
	
	@Test
	public void testPositionObject(){
	}
	
	@Test
	public void testDrawRectangle(){
	}
	
	@Test
	public void testDrawImage(){
	}
	
	@Test
	public void testFill(){
	}
	
	@Test
	public void testSetColor(){
	}
	
	@Test
	public void testIsRenderOnlyInside(){
	}
	
	@Test
	public void testSetRenderOnlyInside(){
	}
	
	@Test
	public void testGetWidth(){
	}
	
	@Test
	public void testGetHeight(){
	}
	
	@Test
	public void testGetRatioWH(){
	}
	
	@Test
	public void testGetRatioHW(){
	}
	
	@Test
	public void testWindowToScreenX(){
		assertEquals(coordinateValue, renderer.windowToScreenX(window, renderer.screenToWindowX(window, coordinateValue)), DELTA, "Checking function inverse returns the same value");
	}
	
	@Test
	public void testWindowToScreenY(){
		assertEquals(coordinateValue, renderer.windowToScreenY(window, renderer.screenToWindowY(window, coordinateValue)), DELTA, "Checking function inverse returns the same value");
	}
	
	@Test
	public void testWindowToScreen(){
		assertEquals(coordinateValue, Renderer.windowToScreen(Renderer.screenToWindow(coordinateValue, 50, 300, 1.0 / 200), 50, 1.0 / 300, 200), DELTA, "Checking function inverse returns the same value");
		
		assertEquals(0, Renderer.windowToScreen(50, 50, 1.0 / 300, 200), DELTA, "Checking correct coordinate found");
		assertEquals(100, Renderer.windowToScreen(200, 50, 1.0 / 300, 200), DELTA, "Checking correct coordinate found");
		assertEquals(-100.0 / 3, Renderer.windowToScreen(0, 50, 1.0 / 300, 200), DELTA, "Checking correct coordinate found");
		
		assertEquals(0, Renderer.windowToScreen(0, 0, 1.0 / 200, 200), DELTA, "Checking correct coordinate found");
		assertEquals(200, Renderer.windowToScreen(200, 0, 1.0 / 200, 200), DELTA, "Checking correct coordinate found");
		assertEquals(-200, Renderer.windowToScreen(-200, 0, 1.0 / 200, 200), DELTA, "Checking correct coordinate found");
	}
	
	@Test
	public void testScreenToWindowX(){
		assertEquals(coordinateValue, renderer.screenToWindowX(window, renderer.windowToScreenX(window, coordinateValue)), DELTA, "Checking function inverse returns the same value");
	}
	
	@Test
	public void testScreenToWindowY(){
		assertEquals(coordinateValue, renderer.screenToWindowY(window, renderer.windowToScreenY(window, coordinateValue)), DELTA, "Checking function inverse returns the same value");
	}
	
	@Test
	public void testScreenToWindow(){
		assertEquals(coordinateValue, Renderer.screenToWindow(Renderer.windowToScreen(coordinateValue, 50, 1.0 / 300, 200), 50, 300, 1.0 / 200), DELTA, "Checking function inverse returns the same value");
		
		assertEquals(50, Renderer.screenToWindow(0, 50, 300, 1.0 / 200), DELTA, "Checking correct coordinate found");
		assertEquals(200, Renderer.screenToWindow(100, 50, 300, 1.0 / 200), DELTA, "Checking correct coordinate found");
		assertEquals(0, Renderer.screenToWindow(-100.0 / 3, 50, 300, 1.0 / 200), DELTA, "Checking correct coordinate found");
		
		assertEquals(0, Renderer.screenToWindow(0, 0, 200, 1.0 / 200), DELTA, "Checking correct coordinate found");
		assertEquals(200, Renderer.screenToWindow(200, 0, 200, 1.0 / 200), DELTA, "Checking correct coordinate found");
		assertEquals(-200, Renderer.screenToWindow(-200, 0, 200, 1.0 / 200), DELTA, "Checking correct coordinate found");
	}
	
	@Test
	public void testScreenToGlX(){
		assertEquals(coordinateValue, renderer.screenToGlX(window, renderer.glToScreenX(window, coordinateValue)), DELTA, "Checking function inverse returns the same value");
	}
	
	@Test
	public void testScreenToGlY(){
		assertEquals(coordinateValue, renderer.screenToGlY(window, renderer.glToScreenY(window, coordinateValue)), DELTA, "Checking function inverse returns the same value");
	}
	
	@Test
	public void testScreenToGl(){
		assertEquals(coordinateValue, Renderer.screenToGl(Renderer.glToScreen(coordinateValue, 50, 1.0 / 300, 200, 200), 50, 300, 1.0 / 200, 1.0 / 200), DELTA, "Checking function inverse returns the same value");
		
		// Case of viewport being equal to the screen size and the window, and no viewport offset
		assertEquals(-1, Renderer.screenToGl(0, 0, 200, 1.0 / 200, 1.0 / 200), DELTA, "Checking correct coordinate found");
		assertEquals(0, Renderer.screenToGl(100, 0, 200, 1.0 / 200, 1.0 / 200), DELTA, "Checking correct coordinate found");
		assertEquals(0.5, Renderer.screenToGl(150, 0, 200, 1.0 / 200, 1.0 / 200), DELTA, "Checking correct coordinate found");
	}
	
	@Test
	public void testGlToScreenX(){
		assertEquals(coordinateValue, renderer.glToScreenX(window, renderer.screenToGlX(window, coordinateValue)), DELTA, "Checking function inverse returns the same value");
	}
	
	@Test
	public void testGlToScreenY(){
		assertEquals(coordinateValue, renderer.glToScreenY(window, renderer.screenToGlY(window, coordinateValue)), DELTA, "Checking function inverse returns the same value");
	}
	
	@Test
	public void testGlToScreen(){
		assertEquals(coordinateValue, Renderer.glToScreen(Renderer.screenToGl(coordinateValue, 50, 300, 1.0 / 200, 1.0 / 200), 50, 1.0 / 300, 200, 200), DELTA, "Checking function inverse returns the same value");
	}
	
	/** Run after each test runs, use to clean up resources */
	@AfterEach
	public void end(){
	}
	
	/** Run after all tests run, use to clean up static resources */
	@AfterAll
	public static void done(){
	}
	
}
