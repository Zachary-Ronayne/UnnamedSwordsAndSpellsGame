package zgametest.graphics.camera;

import static org.junit.jupiter.api.Assertions.*;
import static zgametest.Tester.DELTA;

import org.junit.jupiter.api.*;

import zgame.graphics.camera.CameraAxis;

public class CameraAxisTest{

	private CameraAxis axis;
	
	private double coordinateValue;
	
	/** Run one time before all tests occur, use for initializtion of static values */
	@BeforeAll
	public static void init(){
	}
	
	/** Run one time before each test occurs, use for initializtion of values that must be the same before each test */
	@BeforeEach
	public void setup(){
		axis = new CameraAxis();
		coordinateValue = 5;
	}
	
	@Test
	public void testReset(){
	}
	
	@Test
	public void testGetPos(){
	}
	
	@Test
	public void testSetPos(){
	}
	
	@Test
	public void testGetZoomLevel(){
	}
	
	@Test
	public void testGetZoomLevelInverse(){
	}
	
	@Test
	public void testGetZoomFactor(){
	}
	
	@Test
	public void testSetZoomFactor(){
	}
	
	@Test
	public void testGetZoomPower(){
	}
	
	@Test
	public void testSetZoomPower(){
	}
	
	@Test
	public void testZoom(){
	}
	
	@Test
	public void testShift(){
	}
	
	@Test
	public void testGameToScreen(){
		assertEquals(coordinateValue, axis.gameToScreen(axis.screenToGame(coordinateValue)), DELTA, "Checking function inverse returns the same value");
		
		assertEquals(0, axis.gameToScreen(0), DELTA, "Checking coordinate is unchanged with no transformation");

		axis.zoom(1);
		assertEquals(0, axis.gameToScreen(0), DELTA, "Checking coordinate is unchanged with only zooming");
		assertEquals(20, axis.gameToScreen(10), DELTA, "Checking coordinate is changed with zooming");
		axis.shift(5);
		assertEquals(25, axis.gameToScreen(10), DELTA, "Checking coordinate is changed both translation and zooming");

		axis.reset();
		axis.shift(10);
		assertEquals(10, axis.gameToScreen(0), DELTA, "Checking coordinate is changed with only translation");
	}
	
	@Test
	public void testScreenToGame(){
		assertEquals(coordinateValue, axis.screenToGame(axis.gameToScreen(coordinateValue)), DELTA, "Checking function inverse returns the same value");
		
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
