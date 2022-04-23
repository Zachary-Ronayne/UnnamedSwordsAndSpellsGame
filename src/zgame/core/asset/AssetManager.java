package zgame.core.asset;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zgame.core.utils.ZAssetUtils;
import zgame.core.utils.ZStringUtils;

/**
 * A class that defines common functionality between loading resources
 * 
 * @param <A> The type of asset to manage
 */
public abstract class AssetManager<A extends Asset>{
	
	/** A map containing all assets handled by this {@link AssetManager} */
	private Map<String, A> assets;
	
	/** The path to the files where the assets this manager will manage */
	private String folderLoc;
	
	/** The valid file extension, without a '.' for assets in this manager */
	private String fileFormat;
	
	/** Create a new empty {@link AssetManager} */
	public AssetManager(String folderLoc, String fileFormat){
		this.assets = new HashMap<String, A>();
		this.folderLoc = folderLoc;
		this.fileFormat = fileFormat;
	}
	
	/**
	 * Add the given asset to the manager
	 * 
	 * @param asset The asset to add
	 * @param name The name of the asset, also use this name to get the asset with {@link #get(String)}
	 */
	public void add(A asset, String name){
		this.assets.put(name, asset);
	}
	
	/**
	 * Add the asset of the given name to the manager by loading the asset from {@link #folderLoc}. This method assumes the name corresponds to an asset in {@link #folderLoc}
	 * 
	 * @param name The name of the asset, also use this name to get the asset
	 */
	public void add(String name){
		this.add(this.create(ZStringUtils.concat(name, ".", this.fileFormat)), name);
	}
	
	/**
	 * Get an asset from this manager based on its name
	 * 
	 * @param name The name of the asset to get
	 * @return The asset, or null if the asset does not exist
	 */
	public A get(String name){
		return this.assets.get(name);
	}
	
	/**
	 * Remove an asset from this manager based on its name
	 * 
	 * @param name The name of the asset to get
	 * @return The asset, or null if the asset does not exist
	 */
	public A remove(String name){
		return this.assets.remove(name);
	}
	
	/** @return A collection of every asset in this manager */
	public Collection<A> getAll(){
		return this.assets.values();
	}
	
	/**
	 * Load all the assets in {@link #folderLoc} where the name of the file without a file extension is how they will be referred to using {@link #get(String)}
	 * This will only load files with an extension of {@link #fileFormat}
	 */
	public void addAll(){
		List<String> names = ZAssetUtils.getNames(this.folderLoc, true);
		for(String s : names) if(s.endsWith(ZStringUtils.concat(".", this.fileFormat))) this.add(s.substring(0, s.length() - 4));
	}
	
	/**
	 * Create an instance of the asset that this manager managers, getting the file from the given path
	 * 
	 * @param path The path to get the file
	 * @return The object
	 */
	public abstract A create(String path);
	
	/** Free any resources used by this {@link AssetManager} */
	public void destroy(){
		for(A a : this.getAll()) a.destroy();
	}
	
}
