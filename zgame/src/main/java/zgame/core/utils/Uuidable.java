package zgame.core.utils;

import zgame.things.Tag;

/** Represents an object which has a uuid and can have tags associated with it */
public interface Uuidable{
	/** @return The uuid of this class */
	String getUuid();
	
	/**
	 * @param u Another uuidable object to check
	 * @return true if these represent the same object, false otherwise
	 */
	default boolean matches(Uuidable u){
		if(u == null) return false;
		return this.getUuid().equals(getUuid());
	}
	
	/**
	 * Determine if this object has the given tag
	 *
	 * @param tag The tag to check for
	 * @return true if it has the tag, false otherwise
	 */
	boolean hasTag(Tag tag);
}
