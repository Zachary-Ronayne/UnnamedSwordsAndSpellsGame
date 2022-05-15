package zgame.core.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Utility methods for working with arrays. Repurposed code from one of my other projects, Zab Guitar Maker */
public class ZArrayUtils{
	
	/**
	 * Add the given element to the given {@link List}, inserting it into a position so that the list remains sorted.
	 * 
	 * @param <E> The type of the lists, must be {@link Comparable}
	 * @param arr A sorted List
	 * @param e The element to add
	 * @param allowDuplicates true to allow duplicate values to be added, false to not add them.
	 *        If the value to add is a duplicate and this parameter is true, nothing happens and this method returns false
	 * @return true if the insert is successful, false otherwise
	 */
	public static <E extends Comparable<E>> boolean insertSorted(List<E> arr, E e, boolean allowDuplicates){
		// Find the index to place
		int low = binarySearch(arr, e, true);
		
		// If duplicates are not allowed, and that index contains the given value, return false
		if(!allowDuplicates && (low < arr.size() && arr.get(low).compareTo(e) == 0)) return false;
		
		arr.add(low, e);
		return true;
	}
	
	/**
	 * Add the given element to the given {@link ArrayList}, inserting it into a position so that the list remains sorted. Objects which compare to the same value are not
	 * guaranteed to be in any particular order
	 * 
	 * @param <E> The type of the lists, must be {@link Comparable}
	 * @param arr A sorted ArrayList
	 * @param e The element to add
	 * @return true if the insert is successful, false otherwise
	 */
	public static <E extends Comparable<E>> boolean insertSorted(ArrayList<E> arr, E e){
		return insertSorted(arr, e, true);
	}
	
	/**
	 * Search through a sorted list with comparable values via binary search for the specified element
	 * 
	 * @param <E> The type of elements
	 * @param arr The List to search, must be sorted. If arr is unsorted, this method has undefined behavior
	 * @param e The value to look for
	 * @param findInsert true to return the location of where the element should be inserted to remain sorted if the element is not found, false to return -1 if the value isn't
	 *        found
	 * @return The index of the value, or if the value is not found, then if findInsert is true the index of where that element
	 *         would lie if it were to be inserted into the list in a sorted manor, or -1 if findInsert is false
	 */
	public static <E extends Comparable<E>> int binarySearch(List<E> arr, E e, boolean findInsert){
		int low = 0;
		int high = arr.size();
		int mid = 0;
		
		// Track the middle element
		E midO;
		
		// Loop until low and high converge on one value
		while(low < high){
			// Get the middle index and middle element
			mid = (low + high) >> 1;
			midO = arr.get(mid);
			
			// Compare the to add element to the middle element
			int comp = e.compareTo(midO);
			
			// If the element is before mid, mid becomes the new high
			if(comp < 0) high = mid;
			// If the element is after mid, one after mid becomes the new low
			else if(comp > 0) low = mid + 1;
			// Otherwise, the element is the same as mid, the index is found
			else return mid;
		}
		// The element was not found, the insert index is the low point of the two ends, or return -1 as an error
		if(findInsert) return low;
		else return -1;
	}
	
	/**
	 * Search through a sorted list with comparable values via binary search for the specified element
	 * 
	 * @param <E> The type of elements
	 * @param arr The List to search, must be sorted. If arr is unsorted, this method has undefined behavior
	 * @param e The value to look for
	 * @return The index of the value, or -1 if the element is not found
	 */
	public static <E extends Comparable<E>> int binarySearch(List<E> arr, E e){
		return binarySearch(arr, e, false);
	}
	
	/**
	 * Add the given element to the list only if the given list does not contain the given element
	 * 
	 * @param <E> The type of the elements in the list
	 * @param list The list
	 * @param e The element to potentially add
	 * @return true if the item was added, false otherwise
	 */
	public static <E> boolean addWithoutDuplicate(List<E> list, E e){
		if(list.contains(e)) return false;
		return list.add(e);
	}
	
	/**
	 * Add the all of the given elements to the given list, but only the items which the given list does not contain
	 * 
	 * @param <E> The type of the elements in the list
	 * @param list The list to add items to
	 * @param toAdd The items to potentially add
	 * @return true if all items were added, false otherwise
	 */
	public static <E> boolean addManyWithoutDuplicate(List<E> list, Collection<E> toAdd){
		boolean success = true;
		for(E e : toAdd){ if(!addWithoutDuplicate(list, e)) success = false; }
		return success;
	}
	
	/** Cannot instantiate {@link ZArrayUtils} */
	private ZArrayUtils(){
	}
}
