package zgame.core.graphics;

/** An object that has resources which take up memory that need to be manually deallocated */
public interface Destroyable{
	
	/** Called when this object needs to have it's resources freed. After calling this method, any allocated resources used by this object should no longer use any memory */
	public void destroy();
	
}
