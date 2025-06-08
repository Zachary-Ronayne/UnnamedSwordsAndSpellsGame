package zgame.core.window;

import java.util.Collection;
import java.util.HashMap;

/** A class for managing potential multiple windows from a static reference */
public class WindowManager{
	
	/** The singleton instance of window manager */
	private static WindowManager instance;
	
	/** A mapping of an arbitrary string id to a window */
	private final HashMap<String, GameWindow> windows;
	
	/** Initialize the {@link WindowManager} to a blank state */
	private WindowManager(){
		this.windows = new HashMap<>();
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
	 */
	public void removeWindow(String id){
		this.windows.remove(id);
	}
	
	/**
	 * Attempt to remove the given window from the manager.
	 * The caller of this method is responsible for destroying the window if needed.
	 *
	 * @param window The reference to the actual window object to remove
	 */
	public void removeWindow(GameWindow window){
		// If the given window is found, remove it, otherwise, do nothing
		String foundId = null;
		for(var e : this.windows.entrySet()){
			if(e.getValue() == window){
				foundId = e.getKey();
				break;
			}
		}
		if(foundId != null) this.windows.remove(foundId);
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
