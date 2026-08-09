package zgame.things.core;

/** Ab object which has a state that can be updated */
public interface Stateable{
	/** Move the next state on to the current state */
	void updateState();
}
