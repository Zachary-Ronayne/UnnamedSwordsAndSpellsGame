package zgame.things.entity.state.vector;

import zgame.physics.ZVector;

import java.util.Comparator;
import java.util.HashMap;

/** Represents a mapping of keys to a list of associated actions that will apply to a vector */
public class VectorUpdateMap<K, V extends ZVector<V>>{
	
	/** The updates to apply */
	private final HashMap<K, VectorUpdateList<V>> updates;
	
	/** Build a new empty mapping of updates */
	public VectorUpdateMap(){
		this.updates = new HashMap<>();
	}
	
	/**
	 * Add the given update as one for this list of updates
	 *
	 * @param key The identifier for the vector to update
	 * @param update The update to apply
	 */
	public void update(K key, VectorUpdate<V> update){
		this.updates
				.computeIfAbsent(key, k -> new VectorUpdateList<>())
				.update(update);
	}
	
	/**
	 * Apply the updates of this to the given vector
	 *
	 * @param key The identifier for the vector to apply updates to
	 * @param baseVector The vector to start with
	 * @return The new vector
	 */
	public V apply(K key, V baseVector){
		return this.updates.get(key).apply(baseVector);
	}
	
	/**
	 * Apply the updates of this to the vectors in the given map
	 *
	 * @param mappings A mapping of all vectors which the resulting vectors will be put into
	 */
	public void applyAll(HashMap<K, V> mappings){
		for(var key : this.updates.keySet()){
			var baseForce = mappings.get(key);
			mappings.put(key, this.apply(key, baseForce));
		}
		this.updates.clear();
	}
	
}
