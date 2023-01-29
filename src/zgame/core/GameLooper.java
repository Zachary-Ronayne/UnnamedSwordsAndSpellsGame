package zgame.core;

import zgame.core.utils.ZConfig;
import zgame.core.utils.ZLambda;
import zgame.core.utils.ZStringUtils;
import zgame.core.utils.ZLambda.EmptyFunc;
import zgame.core.utils.ZLambda.BooleanFunc;

/**
 * A class that handles running a loop at a consistent interval, primarily the main OpenGL loop and the tick loop for updating the game
 */
public class GameLooper{
	
	/** The number of nanoseconds in a second */
	public static final long NANO_SECOND = (long)1E9;
	
	/** The number of times per second which this loop will activate. Use zero to run as many times per second as possible */
	private int rate;
	/** The amount of time, in seconds, which each loop iteration is expected to take */
	private double rateTime;
	/** The amount of time, in nanoseconds, which each loop iteration is expected to take */
	private long rateTimeNano;
	/** The timestamp, in nanoseconds, of the last time the loop activated */
	private long lastFunCall;
	
	/** The number of times the loop has been activated since the last time it printed the calculated rate. It prints once time each second */
	private int funcCalls;
	/** The amount of time spent processing, not waiting, in the loop over the last second */
	private long timeProcessing;
	/** The number of nanoseconds the loop last took to run, averaged over the last second */
	private long nsPerLoop;
	/** true to include {@link #nsPerLoop} in the statement from {@link #printRate} */
	private boolean printNsPerLoop;
	/** true if the current rate should be printed once each second, false otherwise */
	private boolean printRate;
	/** The name associated with this looper, this is only used for printing purposes */
	private String name;
	
	/**
	 * The function to call every time this loop runs. Can set to null to automatically use a method that does nothing
	 */
	private EmptyFunc runFunc;
	/**
	 * An additional function that determines if {@link #runFunc} should run regardless of how much time has passed since the last loop. Can set to null to automatically use a
	 * method that always returns false
	 */
	private BooleanFunc shouldRunFunc;
	/**
	 * A function that checks if the loop should automatically end. If this function returns false, this loop will quit out on the next loop iteration. Can set to null to
	 * automatically use a method that always returns true
	 */
	private BooleanFunc keepRunningFunc;
	
	/**
	 * A function which returns true if this function should use the amount of time it takes to run each loop to call Thread.wait between calls of {@link #runFunc} to avoid
	 * wasting resources. It returns false otherwise. This will do nothing if {@link #rate} is set to 0 Can set to null to automatically use a method that always returns
	 * false
	 */
	private BooleanFunc waitBetweenLoopsFunc;
	
	/** true if this looper should end on the next iteration */
	private boolean forceEnd;
	
	/** Keeps track of if the loop is currently running */
	private boolean running;
	
	/**
	 * Create a new GameLooper. The loop will not run until {@link #loop()} is called
	 *
	 * @param rate See {@link #rate}
	 * @param runFunc See {@link #runFunc}
	 * @param shouldRunFunc See {@link #shouldRunFunc}
	 * @param keepRunningFunc See {@link #keepRunningFunc}
	 * @param waitBetweenLoops See {@link #waitBetweenLoopsFunc}
	 * @param name See {@link #name}
	 * @param printRate See {@link #printRate}
	 */
	public GameLooper(int rate, EmptyFunc runFunc, BooleanFunc shouldRunFunc, BooleanFunc keepRunningFunc, BooleanFunc waitBetweenLoops, String name, boolean printRate){
		this.setRate(rate);
		this.lastFunCall = 0;
		this.funcCalls = 0;
		this.setPrintRate(printRate);
		this.setName(name);
		this.setRunFunc(runFunc);
		this.setShouldRunFunc(shouldRunFunc);
		this.setKeepRunningFunc(keepRunningFunc);
		this.setWaitBetweenLoopsFunc(waitBetweenLoops);
		
		this.nsPerLoop = 0;
		this.setPrintNsPerLoop(true);
		
		this.forceEnd = false;
		this.running = false;
	}
	
	/**
	 * Create a new GameLooper. The loop will not run until {@link #loop()} is called
	 *
	 * @param rate See {@link #rate}
	 * @param runFunc See {@link #runFunc}
	 * @param shouldRunFunc See {@link #shouldRunFunc}
	 * @param keepRunningFunc See {@link #keepRunningFunc}
	 */
	public GameLooper(int rate, EmptyFunc runFunc, BooleanFunc shouldRunFunc, BooleanFunc keepRunningFunc){
		this(rate, runFunc, shouldRunFunc, keepRunningFunc, null, "Looper Rate", false);
	}
	
	/** Begin the loop */
	public void loop(){
		this.lastFunCall = System.nanoTime();
		long thisTime;
		long lastTime = System.nanoTime();
		
		// Keep track of the amount of time the last loop took to run
		long timeTaken = 0;
		
		this.running = true;
		while(this.getKeepRunningFunc().check() && !this.forceEnd){
			// If the rate is zero, or if the loop should run regardless, or enough time has passed since the last loop, run the loop again
			thisTime = System.nanoTime();
			if(this.getRate() == 0 || this.getShouldRunFunc().check() || thisTime - this.lastFunCall >= this.getRateTime() * NANO_SECOND){
				this.getRunFunc().run();
				this.funcCalls++;
				timeTaken = thisTime - this.lastFunCall;
				this.lastFunCall = System.nanoTime();
				
				// Keep track of the amount of time spent in the function calls
				this.timeProcessing += this.lastFunCall - thisTime;
			}
			// Print the rate once a second
			if(thisTime - lastTime >= NANO_SECOND){
				this.nsPerLoop = this.timeProcessing / this.funcCalls;
				if(this.willPrintRate()){
					if(this.isPrintNsPerLoop()){
						ZStringUtils.print(ZStringUtils.pad(ZStringUtils.concat(this.getName(), ": ", this.getFuncCalls()), 10), " |\t",
								ZStringUtils.pad(ZStringUtils.concat("ns/loop", ": ", this.getNsPerLoop()), 30), "\t",
								ZStringUtils.pad(ZStringUtils.concat("ns total", ": ", this.timeProcessing), 30), " |\t",
								ZStringUtils.pad(ZStringUtils.concat("ms/loop", ": ", this.getMsPerLoop()), 10), "\t",
								ZStringUtils.pad(ZStringUtils.concat("ms total", ": ", (long)(this.timeProcessing * 1E-6)), 10));
					}
					else ZStringUtils.print(this.getName(), ": ", this.getFuncCalls());
					this.timeProcessing = 0;
				}
				this.funcCalls = 0;
				lastTime = System.nanoTime();
			}
			// Wait between loops to avoid wasting resources
			if(rate != 0 && this.getWaitBetweenLoopsFunc().check()){
				try{
					Thread.sleep(Math.max(0, (long)((this.getRateTimeNano() - timeTaken) / 1E5)));
				}catch(Exception e){
					if(ZConfig.printErrors()){
						System.err.println("Error in waiting for a thread in GameLooper.loop");
						e.printStackTrace();
					}
				}
			}
		}
		this.running = false;
		
	}
	
	/** Force this loop to stop */
	public void end(){
		this.forceEnd = true;
	}
	
	/** @return See {@link #running} */
	public boolean isRunning(){
		return this.running;
	}
	
	/** @return See {@link #rate} */
	public int getRate(){
		return this.rate;
	}
	
	/** @param rate See {@link #rate} */
	public void setRate(int rate){
		this.rate = rate;
		this.rateTime = 1.0 / this.rate;
		this.rateTimeNano = (long)(rateTime * NANO_SECOND);
	}
	
	/** @return See {@link #rateTime} */
	public double getRateTime(){
		return this.rateTime;
	}
	
	/** @return See {@link #rateTimeNano} */
	public double getRateTimeNano(){
		return this.rateTimeNano;
	}
	
	/** @return See {@link #lastFunCall} */
	public long getLastFunCall(){
		return this.lastFunCall;
	}
	
	/** @return See {@link #funcCalls} */
	public int getFuncCalls(){
		return this.funcCalls;
	}
	
	/** @return See {@link #nsPerLoop} */
	public long getNsPerLoop(){
		return this.nsPerLoop;
	}
	
	/** @return The same as {@link #nsPerLoop}, but in milliseconds */
	public long getMsPerLoop(){
		return (long)(this.getNsPerLoop() * 1E-6);
	}
	
	/** @return See {@link #printNsPerLoop} */
	public boolean isPrintNsPerLoop(){
		return this.printNsPerLoop;
	}
	
	/** @param printNsPerLoop See {@link #printNsPerLoop} */
	public void setPrintNsPerLoop(boolean printNsPerLoop){
		this.printNsPerLoop = printNsPerLoop;
	}
	
	/** @return See {@link #printRate} */
	public boolean willPrintRate(){
		return this.printRate;
	}
	
	/** @param printRate See {@link #printRate} */
	public void setPrintRate(boolean printRate){
		this.printRate = printRate;
	}
	
	/** @return See {@link #name} */
	public String getName(){
		return this.name;
	}
	
	/** @param name See {@link #name} */
	public void setName(String name){
		this.name = name;
	}
	
	/** @return See {@link #runFunc} */
	public EmptyFunc getRunFunc(){
		return this.runFunc;
	}
	
	/** @param runFunc See {@link #runFunc} */
	public void setRunFunc(EmptyFunc runFunc){
		if(runFunc == null) runFunc = ZLambda::emptyMethod;
		this.runFunc = runFunc;
	}
	
	/** @return See {@link #shouldRunFunc} */
	public BooleanFunc getShouldRunFunc(){
		return this.shouldRunFunc;
	}
	
	/** @param shouldRunFunc See {@link #shouldRunFunc}. */
	public void setShouldRunFunc(BooleanFunc shouldRunFunc){
		if(shouldRunFunc == null) shouldRunFunc = () -> false;
		this.shouldRunFunc = shouldRunFunc;
	}
	
	/** @return See {@link #keepRunningFunc} */
	public BooleanFunc getKeepRunningFunc(){
		return this.keepRunningFunc;
	}
	
	/** @param keepRunningFunc See {@link #keepRunningFunc} */
	public void setKeepRunningFunc(BooleanFunc keepRunningFunc){
		if(keepRunningFunc == null) keepRunningFunc = () -> true;
		this.keepRunningFunc = keepRunningFunc;
	}
	
	/** @return See {@link #waitBetweenLoopsFunc} */
	public BooleanFunc getWaitBetweenLoopsFunc(){
		return this.waitBetweenLoopsFunc;
	}
	
	/** @param waitBetweenLoopsFunc See {@link #waitBetweenLoopsFunc} */
	public void setWaitBetweenLoopsFunc(BooleanFunc waitBetweenLoopsFunc){
		if(waitBetweenLoopsFunc == null) waitBetweenLoopsFunc = () -> false;
		this.waitBetweenLoopsFunc = waitBetweenLoopsFunc;
	}
	
}
