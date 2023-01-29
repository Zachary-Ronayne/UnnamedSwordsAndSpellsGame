package zgametest;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/** A simple template class for ease of creating new test classes */
public class Tester{
	
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
	
	/** A sample test method */
	@Test
	public void test(){
		assertTrue(true);
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
