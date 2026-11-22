package zgame.things.entity.state.vector;

import zgame.physics.ZVector;

import java.util.ArrayList;
import java.util.Comparator;

/** Represents a list of actions that will apply to a vector */
public class VectorUpdateList<V extends ZVector<V>>{
	
	/** The updates to apply */
	private final ArrayList<VectorUpdate<V>> updates;
	
	/** Build a new empty list of updates */
	public VectorUpdateList(){
		this.updates = new ArrayList<>();
	}
	
	/**
	 * Add the given update as one for this list of updates
	 *
	 * @param update The update to add
	 */
	public void update(VectorUpdate<V> update){
		this.updates.add(update);
	}
	
	/**
	 * Apply the updates of this to the given vector and remove all updates
	 *
	 * @param baseVector The vector to start with
	 * @return The new vector
	 */
	public V apply(V baseVector){
		var sortedUpdates = this.updates.stream().sorted(Comparator.comparingDouble(VectorUpdate::priority)).toList();
		var newVec = baseVector;
		for(var update : sortedUpdates){
			newVec = update.apply(newVec);
		}
		this.updates.clear();
		return newVec;
	}
	
}
