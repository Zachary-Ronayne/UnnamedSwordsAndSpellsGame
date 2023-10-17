package zgame.core.input;

import zgame.core.Game;

import java.util.HashMap;
import java.util.Map;

/** A map of {@link InputHandler} objects to track a bunch of them at once */
public class InputHandlers{
	
	/** The handlers this object tracks */
	private final Map<Integer, InputHandler> handlers;
	
	/**
	 * Create a map of handlers with the given handler list
	 *
	 * @param handlers See #handlers
	 */
	public InputHandlers(InputHandler... handlers){
		this.handlers = new HashMap<>();
		for(var h : handlers) this.handlers.put(h.getInputKey(), h);
	}
	
	/** @param handler A new handler to track */
	public void add(InputHandler handler){
		this.handlers.put(handler.getInputKey(), handler);
	}
	
	/** @param index {@link InputHandler#inputKey} to stop tracking */
	public void remove(int index){
		this.handlers.remove(index);
	}
	
	/**
	 * Determine if this object is tracking the given index
	 *
	 * @param index The index to check
	 * @return true if this object is tracking the index, false otherwise
	 */
	public boolean has(int index){
		return this.handlers.containsKey(index);
	}
	
	/**
	 * @param index The index to check
	 * @return The value of {@link InputHandler#pressed} of the given index
	 */
	public boolean pressed(int index){
		return this.handlers.get(index).isPressed();
	}
	
	/**
	 * Perform {@link InputHandler#tick(Game)} on the given index. Does not check if the mapping exists, and will throw an exception if the mapping does not exist
	 *
	 * @param game The game
	 * @param index the index
	 * @return true if the handler was pressed, false otherwise
	 */
	public boolean tick(Game game, int index){
		return this.handlers.get(index).tick(game);
	}
	
}
