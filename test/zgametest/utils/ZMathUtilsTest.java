package zgametest.utils;

import org.junit.jupiter.api.*;

import zgame.core.utils.ZMathUtils;

import static org.junit.jupiter.api.Assertions.*;

public class ZMathUtilsTest{
	
	/** A simple constant for testing if double precision floating point values are approximately equal */
	public static double DELTA = 0.000000001;
	
	/** Run one time before all tests occur, use for initialization of static values */
	@BeforeAll
	public static void init(){
	}
	
	/** Run one time before each test occurs, use for initialization of values that must be the same before each test */
	@BeforeEach
	public void setup(){
	}
	
	@Test
	public void testLineAngle(){
		assertEquals(0, ZMathUtils.lineAngle(1, 10, 2, 10), DELTA);
		assertEquals(Math.PI * 0.25, ZMathUtils.lineAngle(1, 10, 2, 11), DELTA);
		assertEquals(Math.PI * 0.5, ZMathUtils.lineAngle(1, 10, 1, 11), DELTA);
		assertEquals(Math.PI * 0.75, ZMathUtils.lineAngle(2, 10, 1, 11), DELTA);
		assertEquals(Math.PI, ZMathUtils.lineAngle(2, 10, 1, 10), DELTA);
		assertEquals(-Math.PI * .75, ZMathUtils.lineAngle(2, 11, 1, 10), DELTA);
		assertEquals(-Math.PI * .5, ZMathUtils.lineAngle(1, 11, 1, 10), DELTA);
		assertEquals(-Math.PI * .25, ZMathUtils.lineAngle(1, 11, 2, 10), DELTA);
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
