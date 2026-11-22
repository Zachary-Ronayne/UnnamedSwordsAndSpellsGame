package zgame.core.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * A map of lists, where each list contains all the objects added to this object. For each class added to this object, a new list will be created.
 * This object will never store null values
 * All classes intended to be used should be added before adding any elements.
 * If the given object is {@link Uuidable}, then it will also be stored as a map.
 * If the object implements {@link Comparable}, then the list will be sorted in ascending order.
 * Essentially, this class allows objects to be added to an object, and then any object of any pre-specified type can be returned as a list or map of those objects.
 */
public class ClassMappedList{
	/** The map of this object. Key: The class, value: A list of all elements of that class which have been added to this object */
	private final Map<Class<?>, NotNullList<?>> map;
	
	/** The map of maps, mapped by uuid */
	private final Map<Class<?>, Map<String, ?>> uuidMap;
	
	/** Create the new empty object */
	public ClassMappedList(){
		this.map = new HashMap<>();
		this.uuidMap = new HashMap<>();
	}
	
	/** @return The map this class internally keeps track of */
	public Map<Class<?>, NotNullList<?>> get(){
		return map;
	}
	
	/**
	 * Get one of the lists in this object
	 *
	 * @param clazz The type of object to get a list of
	 * @param <T> The type of class
	 * @return The list, or null if none exist for the given class
	 */
	@SuppressWarnings("unchecked")
	public <T> NotNullList<T> get(Class<T> clazz){
		if(!this.map.containsKey(clazz)) return null;
		return (NotNullList<T>)this.map.get(clazz);
	}
	
	/**
	 * Get one of the maps in this object
	 *
	 * @param clazz The type of object to get a map of
	 * @param <T> The type of class
	 * @return The map, or null if none exist for the given class
	 */
	@SuppressWarnings("unchecked")
	public <T> HashMap<String, T> getMap(Class<T> clazz){
		if(!this.uuidMap.containsKey(clazz)) return null;
		return (HashMap<String, T>)this.uuidMap.get(clazz);
	}
	
	/**
	 * Add an element to this object
	 *
	 * @param obj The element to add
	 * @param <T> The type of the element to add
	 * @return true if the thing was added, false otherwise
	 */
	@SuppressWarnings("unchecked")
	public <T> boolean add(T obj){
		if(obj == null) return false;
		
		// Add it to the list
		var canCompare = Comparable.class.isAssignableFrom(obj.getClass());
		for(var c : this.map.keySet()){
			if(c.isInstance(obj)) {
				var list = (NotNullList<T>)this.get(c);
				if(canCompare) ZArrayUtils.insertSorted(list, obj, (a, b) -> ((Comparable<T>)a).compareTo(b));
				else list.add(obj);
			}
		}
		
		// Add it to the uuid map
		for(var c : this.uuidMap.keySet()){
			if(c.isInstance(obj)){
				var mapMap = (Map<String, T>)this.getMap(c);
				mapMap.put(((Uuidable)obj).getUuid(), obj);
			}
		}
		return true;
	}
	
	/**
	 * Remove an object from this object. The object will be removed from all lists
	 *
	 * @param obj The object to remove
	 * @param <T> The type of the object to remove
	 * @return true if the object was removed, false otherwise
	 */
	public <T> boolean remove(T obj){
		var removed = true;
		var found = false;
		for(var c : this.map.keySet()){
			removed &= this.get(c).remove(obj);
			found = true;
		}
		for(var c : this.uuidMap.keySet()){
			if(c.isInstance(obj)) {
				removed &= this.getMap(c).remove(((Uuidable)obj).getUuid()) != null;
				found = true;
			}
		}
		return removed && found;
	}
	
	/**
	 * Remove all objects of the given type from this object
	 * @param clazz The class of objects to remove
	 * @param <T> The type of clazz
	 */
	public <T> void removeAll(Class<T> clazz){
		var list = get(clazz);
		if(list != null) list.clear();
		var map = getMap(clazz);
		if(map != null) map.clear();
	}
	
	/**
	 * Tell this object to keep track of a particular type of object
	 *
	 * @param clazz The class to keep track of
	 * @param <T> The type of the object
	 */
	public <T> void addClass(Class<T> clazz){
		if(!this.map.containsKey(clazz)) this.map.put(clazz, new NotNullList<>());
		if(!this.uuidMap.containsKey(clazz) && Uuidable.class.isAssignableFrom(clazz)) this.uuidMap.put(clazz, new HashMap<>());
	}
	
}
