package zgame.core.asset;

import zgame.core.graphics.Destroyable;

/** An object contains common functionality for assets. An Asset is some kind of file loaded from the .jar */
public abstract class Asset implements Destroyable{
	
	/** The path used to obtain this asset */
	private final String path;
	
	/**
	 * Create an asset which comes from the given path
	 *
	 * @param path The file path to the asset
	 */
	public Asset(String path){
		this.path = path;
	}
	
	/** @return See {@link #path} */
	public String getPath(){
		return this.path;
	}
	
}
