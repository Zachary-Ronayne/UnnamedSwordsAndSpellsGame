package zgame.core.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A list of objects which cannot contain null elements
 */
public class NotNullList<T> extends ArrayList<T>{
	
	/** Create a new empty list */
	public NotNullList(){
		super();
	}
	
	/**
	 * Create a new list initially with the given elements
	 */
	@SafeVarargs
	public NotNullList(T... arr){
		this();
		this.addAll(List.of(arr));
	}
	
	/**
	 * Add the given element to the list. Does nothing if element is null
	 *
	 * @param element The element to add
	 * @return true if the element was added, false otherwise
	 */
	public boolean add(T element){
		if(element == null) return false;
		return super.add(element);
	}
	
	/**
	 * Add the given element to the list. Does nothing if element is null
	 *
	 * @param index The index to add the element to
	 * @param element The element to add
	 */
	@Override
	public void add(int index, T element){
		if(element == null) return;
		super.add(index, element);
	}
	
	/**
	 * Add the given elements to the list. Does nothing if element is null
	 *
	 * @param c The elements to add, null elements are ignoredReturns:
	 * @return true if this list changed as a result of the call
	 */
	@Override
	public boolean addAll(Collection<? extends T> c){
		Collection<T> toAdd = new ArrayList<>(c.size());
		for(T t : c) if(t != null) toAdd.add(t);
		return super.addAll(toAdd);
	}
	
	/**
	 * Add the given elements to the list. Does nothing if element is null
	 *
	 * @param index index at which to insert the first element from the specified collection
	 * @param c The elements to add, null elements are ignored
	 * @return true if this list changed as a result of the call
	 */
	@Override
	public boolean addAll(int index, Collection<? extends T> c){
		Collection<T> toAdd = new ArrayList<>(c.size());
		for(T t : c) if(t != null) toAdd.add(t);
		return super.addAll(index, toAdd);
	}
	
	/**
	 * Set the given element at the given list index. Does nothing if element is null
	 *
	 * @param index The index to set the element
	 * @param element The element to set
	 * @return The replaced element, or null if element is null
	 */
	@Override
	public T set(int index, T element){
		if(element == null) return null;
		return super.set(index, element);
	}
	
	/**
	 * Remove the given object from this list
	 *
	 * @param o The object to remove
	 * @return true if the object was removed, false otherwise. Always returns false if o is null
	 */
	@Override
	public boolean remove(Object o){
		if(o == null) return false;
		return super.remove(o);
	}
	
}
