package zgame.things.core.state;

import java.util.ArrayList;
import java.util.function.Supplier;

/** Object holding multiple state objects */
public class StateList{
	
	/** All states managed by this list */
	private final ArrayList<StateHolder<?>> states;
	
	/** Create an empty state list managing no states */
	public StateList(){
		this.states = new ArrayList<>();
	}
	
	/**
	 * @param initState A state holder that should be managed by this list
	 * @return The function used to initialize the state
	 * @param <T> The type of state to manage
	 */
	public <T extends GameThingState> StateHolder<T> register(Supplier<T> initState){
		var holder = new StateHolder<T>(initState);
		this.states.add(holder);
		return holder;
	}
	
	/** Update all states to their next values */
	public void updateStates(){
		for(var s : this.states) s.updateState();
	}
	
}
