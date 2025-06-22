package zgame.core.window;

import zgame.core.graphics.Destroyable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/** A class for managing potential multiple windows from a static reference */
public class WindowManager implements Destroyable{
	
	/** The singleton instance of window manager */
	private static WindowManager instance;
	
	/** A mapping of an arbitrary string id to a window */
	private final HashMap<String, GameWindow> windows;
	
	/** All windows which should be removed from the manager when next possible */
	private final HashSet<String> windowsToRemove;
	
	/** The last window which was used for the current context */
	private GameWindow lastContext;
	
	/** Initialize the {@link WindowManager} to a blank state */
	private WindowManager(){
		this.windows = new HashMap<>();
		this.windowsToRemove = new HashSet<>();
		this.lastContext = null;
	}
	
	@Override
	public void destroy(){
		var windows = WindowManager.get().getWindows().stream().toList();
		for(var window : windows){
			WindowManager.get().removeWindow(window);
			window.destroy();
		}
	}
	
	/**
	 * Obtain a {@link GameWindow} from the manager
	 *
	 * @param id The id of the window
	 * @return The window, or null if none exists
	 */
	public GameWindow getWindow(String id){
		return this.windows.get(id);
	}
	
	/** @return All windows managed by the manager. Should be treated as read only */
	public Collection<GameWindow> getWindows(){
		return this.windows.values();
	}
	
	/**
	 * Find the windowId of the given window
	 *
	 * @param window The window object to look for its id
	 * @return The id, or null if it is not in the window
	 */
	public String findWindowId(GameWindow window){
		for(var e : this.windows.entrySet()){
			if(e.getValue() == window) return e.getKey();
		}
		return null;
	}
	
	/**
	 * Add the given window to this manager
	 *
	 * @param id An id to later reference the window with
	 * @param window The window to add
	 */
	public void addWindow(String id, GameWindow window){
		this.windows.put(id, window);
	}
	
	/**
	 * Remove the window from the manager with the given id.
	 * The caller of this method is responsible for destroying the window if needed.
	 *
	 * @param id The id of the window to remove
	 * @return The removed window, or null if none was removed
	 */
	private GameWindow removeWindow(String id){
		return this.windows.remove(id);
	}
	
	/**
	 * Attempt to remove the given window from the manager.
	 * The caller of this method is responsible for destroying the window if needed.
	 *
	 * @param window The reference to the actual window object to remove
	 * @return The removed window, or null if none was removed
	 */
	private GameWindow removeWindow(GameWindow window){
		// If the given window is found, remove it, otherwise, do nothing
		var foundId = this.findWindowId(window);
		if(foundId != null) return this.removeWindow(foundId);
		return null;
	}
	
	/**
	 * Declare that the given window should be removed from the manager and destroyed when next possible
	 *
	 * @param window The window to remove
	 */
	public void flagRemoval(GameWindow window){
		var foundId = this.findWindowId(window);
		this.flagRemoval(foundId);
	}
	
	/**
	 * Declare that the window with the given id should be removed from the manager and destroyed when next possible
	 *
	 * @param window The window id to remove
	 */
	public void flagRemoval(String window){
		this.windowsToRemove.add(window);
	}
	
	/** Run the loop function on all windows, and remove any windows which need to be removed */
	public void loopFunction(){
		// Remove any windows scheduled for removal
		if(!this.windowsToRemove.isEmpty()){
			for(var window : this.windowsToRemove){
				var removed = this.removeWindow(window);
				if(removed != null) removed.destroy();
			}
			this.windowsToRemove.clear();
		}
		
		// Get a copy of the list of windows to avoid concurrent modification
		var windowsList = get().getWindows().stream().toList();
		// Update and render all windows
		for(var window : windowsList) {
			if(this.lastContext != window) window.obtainContext();
			window.loopFunction();
			this.lastContext = window;
		}
	}
	
	/** @return See {@link #instance} */
	public static WindowManager get(){
		return instance;
	}
	
	/** Initialize the manager's singleton. Must be called before window management can be used */
	public static void init(){
		if(instance != null) return;
		
		instance = new WindowManager();
	}
}
