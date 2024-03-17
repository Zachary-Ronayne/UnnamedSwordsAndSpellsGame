package zgametest.physics;

import org.junit.jupiter.api.*;

import zgame.physics.ZVector;
import zgame.physics.ZVector2D;

import static org.junit.jupiter.api.Assertions.*;

public class ZVectorTest{
	
	/** A simple constant for testing if double precision floating point values are approximately equal */
	public static double DELTA = 0.000000001;
	
	private ZVector2D vec;
	
	/** Run one time before all tests occur, use for initialization of static values */
	@BeforeAll
	public static void init(){
	}
	
	/** Run one time before each test occurs, use for initialization of values that must be the same before each test */
	@BeforeEach
	public void setup(){
		vec = new ZVector2D(1, 0);
	}
	
	/** A sample test method */
	@Test
	public void angleDegTest(){
		assertEquals(0, vec.getAngleDeg(), DELTA);
		
		vec = new ZVector2D(1, 1);
		assertEquals(45, vec.getAngleDeg(), DELTA);
		
		vec = new ZVector2D(0, 1);
		assertEquals(90, vec.getAngleDeg(), DELTA);
		
		vec = new ZVector2D(-1, 1);
		assertEquals(135, vec.getAngleDeg(), DELTA);
		
		vec = new ZVector2D(-1, 0);
		assertEquals(180, vec.getAngleDeg(), DELTA);
		
		vec = new ZVector2D(-1, -1);
		assertEquals(225, vec.getAngleDeg(), DELTA);
		
		vec = new ZVector2D(0, -1);
		assertEquals(270, vec.getAngleDeg(), DELTA);
		
		vec = new ZVector2D(1, -1);
		assertEquals(315, vec.getAngleDeg(), DELTA);
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
