package zgametest.graphics.camera;

import org.junit.jupiter.api.*;

import zgame.core.graphics.camera.GameCamera;

import static zgametest.Tester.DELTA;

import static org.junit.jupiter.api.Assertions.*;

public class GameCameraTest{
	
	private GameCamera cam;
	private int coordinateValue;
	
	/** Run one time before all tests occur, use for initialization of static values */
	@BeforeAll
	public static void init(){
	}
	
	/** Run one time before each test occurs, use for initialization of values that must be the same before each test */
	@BeforeEach
	public void setup(){
		cam = new GameCamera();
		coordinateValue = 5;
	}
	
	@Test
	public void testReset(){
	}
	
	@Test
	public void testTransform(){
	}
	
	@Test
	public void testGameToScreenX(){
		assertEquals(coordinateValue, cam.gameToScreenX(cam.screenToGameX(coordinateValue)), DELTA, "Checking function inverse returns the same value");
	}
	
	@Test
	public void testGameToScreenY(){
		assertEquals(coordinateValue, cam.gameToScreenY(cam.screenToGameY(coordinateValue)), DELTA, "Checking function inverse returns the same value");
	}
	
	@Test
	public void testScreenToGameX(){
		assertEquals(coordinateValue, cam.screenToGameX(cam.gameToScreenX(coordinateValue)), DELTA, "Checking function inverse returns the same value");
	}
	
	@Test
	public void testScreenToGameY(){
		assertEquals(coordinateValue, cam.screenToGameY(cam.gameToScreenY(coordinateValue)), DELTA, "Checking function inverse returns the same value");
	}
	
	@Test
	public void testGetX(){
	}
	
	@Test
	public void testGetY(){
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
