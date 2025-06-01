package zgametest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import zgame.core.Game;
import zgame.core.window.GameWindow;

public class GameWindowTest{
	
	private static Game game;
	private static GameWindow window;
	
	// private double coordinateValue;
	
	/** Run one time before all tests occur, use for initialization of static values */
	@BeforeAll
	public static void init(){
		game = new TestGame();
		window = game.getWindow();
	}
	
	/** Run one time before each test occurs, use for initialization of values that must be the same before each test */
	@BeforeEach
	public void setup(){
		window.resize(400, 150);
		// coordinateValue = 5;
	}
	
	@Test
	public void testSetSize(){
	}
	
	@Test
	public void testGetWindowTitle(){
	}
	
	@Test
	public void testSetWindowTitle(){
	}
	
	@Test
	public void testGetCurrentWindowID(){
	}
	
	@Test
	public void testGetWindowID(){
	}
	
	@Test
	public void testGetFullScreenID(){
	}
	
	@Test
	public void testIsInFullScreen(){
	}
	
	@Test
	public void testCenter(){
	}
	
	@Test
	public void testGetWindowPos(){
	}
	
	@Test
	public void testGetCurrentMonitor(){
	}
	
	@Test
	public void testSetFullscreen(){
	}
	
	@Test
	public void testToggleFullscreen(){
	}
	
	@Test
	public void testIsStretchToFill(){
	}
	
	@Test
	public void testSetStretchToFill(){
	}
	
	@Test
	public void testGetWidth(){
	}
	
	@Test
	public void testSetWidth(){
	}
	
	@Test
	public void testGetHeight(){
	}
	
	@Test
	public void testSetHeight(){
	}
	
	@Test
	public void testGetScreenWidth(){
	}
	
	@Test
	public void testGetScreenHeight(){
	}
	
	@Test
	public void testGetWindowRatio(){
	}
	
	@Test
	public void testViewport(){
		assertEquals(window.viewportX(), 50, "Checking viewport x initialized");
		assertEquals(window.viewportY(), 0, "Checking viewport y initialized");
		assertEquals(window.viewportW(), 300, "Checking viewport w initialized");
		assertEquals(window.viewportH(), 150, "Checking viewport h initialized");
		
		window.setStretchToFill(true);
		assertEquals(window.viewportX(), 0, "Checking viewport x after using stretch to fill");
		assertEquals(window.viewportY(), 0, "Checking viewport y after using stretch to fill");
		assertEquals(window.viewportW(), 400, "Checking viewport w after using stretch to fill");
		assertEquals(window.viewportH(), 150, "Checking viewport h after using stretch to fill");
		
		window.setStretchToFill(false);
		window.resize(200, 250);
		assertEquals(window.viewportX(), 0, "Checking viewport x with horizontal bars");
		assertEquals(window.viewportY(), 75, "Checking viewport y with horizontal bars");
		assertEquals(window.viewportW(), 200, "Checking viewport x with horizontal bars");
		assertEquals(window.viewportH(), 100, "Checking viewport y with horizontal bars");
	}
	
	/*
	 * @Test
	 * public void testWindowToScreenX(){
	 * assertEquals(coordinateValue, window.windowToScreenX(window.screenToWindowX(coordinateValue)), DELTA, "Checking function inverse returns the same value");
	 * }
	 *
	 * @Test
	 * public void testWindowToScreenY(){
	 * assertEquals(coordinateValue, window.windowToScreenY(window.screenToWindowY(coordinateValue)), DELTA, "Checking function inverse returns the same value");
	 * }
	 *
	 * @Test
	 * public void testScreenToWindowX(){
	 * assertEquals(coordinateValue, window.screenToWindowX(window.windowToScreenX(coordinateValue)), DELTA, "Checking function inverse returns the same value");
	 * }
	 *
	 * @Test
	 * public void testScreenToWindowY(){
	 * assertEquals(coordinateValue, window.screenToWindowY(window.windowToScreenY(coordinateValue)), DELTA, "Checking function inverse returns the same value");
	 * }
	 *
	 * @Test
	 * public void testScreenToGlX(){
	 * assertEquals(coordinateValue, window.screenToGlX(window.glToScreenX(coordinateValue)), DELTA, "Checking function inverse returns the same value");
	 * }
	 *
	 * @Test
	 * public void testScreenToGlY(){
	 * assertEquals(coordinateValue, window.screenToGlY(window.glToScreenY(coordinateValue)), DELTA, "Checking function inverse returns the same value");
	 * }
	 *
	 * @Test
	 * public void testGlToScreenX(){
	 * assertEquals(coordinateValue, window.glToScreenX(window.screenToGlX(coordinateValue)), DELTA, "Checking function inverse returns the same value");
	 * }
	 *
	 * @Test
	 * public void testGlToScreenY(){
	 * assertEquals(coordinateValue, window.glToScreenY(window.screenToGlY(coordinateValue)), DELTA, "Checking function inverse returns the same value");
	 * }
	 */
	
	@Test
	public void testGetMaxFps(){
	}
	
	@Test
	public void testSetMaxFps(){
	}
	
	@Test
	public void testUsesVsync(){
	}
	
	@Test
	public void testSetUseVsync(){
	}
	
	@Test
	public void testGetRenderer(){
	}
	
	@Test
	public void testGetCamera(){
	}
	
	@Test
	public void testIsPrintFps(){
	}
	
	@Test
	public void testSetPrintFps(){
	}
	
	@Test
	public void testGetTps(){
	}
	
	@Test
	public void testSetTps(){
	}
	
	@Test
	public void testIsPrintTps(){
	}
	
	@Test
	public void testSetPrintTps(){
	}
	
	@Test
	public void testGetMouseInput(){
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
