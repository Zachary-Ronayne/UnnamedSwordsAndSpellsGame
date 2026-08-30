package zgame.things.core.state;

import java.util.function.Supplier;

/** An object tracking the current and next state of a thing */
public class StateHolder<State extends GameThingState> implements Stateable{
	
	/** The object holding all values representing the current state of this thing. This object should be treated as read only */
	private State current;
	/** The state which will become {@link #current} after a tick finishes running */
	private State next;
	
	/**
	 * @param initState A method that provides the default state for this thing
	 */
	public StateHolder(Supplier<State> initState){
		this.next = initState.get();
		this.current = initState.get();
	}
	
	/** Move the {@link #next} state on to the {@link #current} state */
	@Override
	public void updateState(){
		var temp = this.current;
		this.current = this.next;
		// TODO need to either have a tick fully overwrite all state on current, or make this method copy the values of current onto next at this point
		temp.copyState(this.next);
		this.next = temp;
	}
	
	/** @return See {@link #current} */
	public State getCurrent(){
		return this.current;
	}
	
	/** @return See {@link #next} */
	public State getNext(){
		return this.next;
	}
	
}
