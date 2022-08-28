package zgame.core.utils;

import java.util.Stack;

/**
 * A type of {@link Stack} that always has at least one element, i.e., if there is only one element left and the stack is popped, do nothing
 * 
 * @param <T> The type of objects in the stack
 */
public class LimitedStack<T>{
	
	/** The {@link Stack} this object uses to keep track of its elements */
	private Stack<T> stack;
	
	/** The item to keep in the stack if the last element is popped */
	private T defaultItem;
	
	/** true if the first element in the stack can be replaced, false if it must always stay the same */
	private boolean replaceFirst;
	
	/**
	 * Create a new limited stack with one element
	 * 
	 * @param initial The initial element in the stack, also the value of {@link #defaultItem}
	 */
	public LimitedStack(T initial){
		this(initial, true);
	}
	
	/**
	 * Create a new limited stack with one element
	 * 
	 * @param initial The initial element in the stack, also the value of {@link #defaultItem}
	 * @param replaceFirst See {@link #replaceFirst}
	 */
	public LimitedStack(T initial, boolean replaceFirst){
		this.stack = new Stack<T>();
		this.defaultItem = initial;
		this.replaceFirst = replaceFirst;
		this.stack.push(this.defaultItem);
	}
	
	/** @return See {@link #defaultItem} */
	public T getDefaultItem(){
		return this.defaultItem;
	}
	
	/**
	 * Set the default bottom of the stack, also replaces the bottom of the stack if {@link #isReplaceFirst()} returns true
	 * 
	 * @param defaultItem See {@link #defaultItem}
	 */
	public void setDefaultItem(T defaultItem){
		this.defaultItem = defaultItem;
		if(!this.isReplaceFirst()) this.stack.set(0, this.defaultItem);
	}
	
	/** @return See {@link #replaceFirst} */
	public boolean isReplaceFirst(){
		return this.replaceFirst;
	}
	
	/** @param replaceFirst See {@link #replaceFirst} */
	public void setReplaceFirst(boolean replaceFirst){
		this.replaceFirst = replaceFirst;
	}
	
	/**
	 * Push the current top of the stack, onto the stack. Be weary of this operation if T is mutable
	 * 
	 * @return The item on the top of the stack
	 */
	public T push(){
		return this.stack.push(this.peek());
	}
	
	/**
	 * Add the given item to the top of the stack
	 * 
	 * @param item The item to add
	 * @return item
	 */
	public T push(T item){
		return this.stack.push(item);
	}
	
	/**
	 * Replace the top of the stack with the given item
	 * 
	 * @param item The item
	 * @return The item that was at the top of the stack, or null if the top couldn't be replaced
	 */
	public T replaceTop(T item){
		if(!this.isReplaceFirst() && this.stack.size() <= 1) return null;
		T old = this.stack.pop();
		this.stack.push(item);
		return old;
	}
	
	/**
	 * Remove the element on the top of this stack. If no element can be popped and {@link #isReplaceFirst()} returns true,
	 * {@link #defaultItem} becomes the only element in the stack
	 * 
	 * @return The removed element, or null if no element could be removed
	 */
	public T pop(){
		if(this.stack.size() <= 1){
			if(this.isReplaceFirst()) this.replaceTop(this.getDefaultItem());
			return null;
		}
		return this.stack.pop();
	}
	
	/** @return The element at the top of this stack */
	public T peek(){
		return this.stack.peek();
	}
	
	/** @return The number of elements in the stack, will always be at least one */
	public int size(){
		return this.stack.size();
	}
	
}
