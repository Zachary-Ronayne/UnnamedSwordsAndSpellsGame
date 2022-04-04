package zgame.core.asset;

/** An object contains common functionality for assets. An Asset is some kind of file loaded from the .jar  */
public abstract class Asset{
	
	/** The path used to obtain this asset */
	private String path;

	/**
	 * Create an asset which comes from the given path
	 * @param path
	 */
	public Asset(String path){
		this.path = path;
	}

	/** @return See {@link #path} */
	public String getPath(){
		return this.path;
	}

	/** Free any resources used by this {@link Asset} */
	public abstract void destroy();
	
}
