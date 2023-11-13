package zgame.core.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.function.Function;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import zgame.core.utils.ZConfig;

/** A representation of a JSON file used for saving and loading games */
public class ZJsonFile{
	
	/** The path leading to the location of this file. Should include .json extension */
	private String path;
	
	/** The data associated with this file */
	private JsonObject data;
	
	/** Create a new {@link ZJsonFile} with the given {@link #path} */
	public ZJsonFile(String path){
		this.path = path;
		this.data = new JsonObject();
	}
	
	/**
	 * Load this {@link ZJsonFile} from {@link #path}, set it in {@link #data},
	 *
	 * @return {@link #data}, or null if the load failed
	 */
	public JsonObject load(){
		File file = this.getFile();
		try(Scanner read = new Scanner(file)){
			// Read in every line from the file
			StringBuilder sb = new StringBuilder();
			while(read.hasNextLine()) sb.append(read.nextLine());
			String jsonText = sb.toString();
			Gson gson = new Gson();
			this.data = gson.fromJson(jsonText, JsonObject.class);
		}catch(FileNotFoundException e){
			ZConfig.error(e, "Failed to open JSON file at path", this.getPath());
			return null;
		}
		return this.data;
	}
	
	/**
	 * Save this {@link ZJsonFile} to {@link #path}
	 *
	 * @return true if the save succeeded, false otherwise
	 */
	public boolean save(){
		File file = this.getFile();
		try(PrintWriter write = new PrintWriter(file)){
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String jsonText = gson.toJson(this.data);
			write.write(jsonText);
		}catch(FileNotFoundException e){
			ZConfig.error(e, "Failed to save JSON file to path", this.getPath());
			return false;
		}catch(UnsupportedOperationException | NullPointerException | ClassCastException | IllegalStateException e){
			ZConfig.error(e, "Failed to save JSON file to path", this.getPath(), "with data:", this.getData());
		}
		return true;
	}
	
	/** @return A {@link File} representing the location at {@link #path}, or null if the file could not be found */
	public File getFile(){
		if(this.getPath() == null) return null;
		return new File(this.getPath());
	}
	
	/** @return See {@link #path} */
	public String getPath(){
		return this.path;
	}
	
	/** @param path See {@link #path} */
	public void setPath(String path){
		this.path = path;
	}
	
	/** @return See {@link #data} */
	public JsonObject getData(){
		return this.data;
	}
	
	/** @param data See {@link #data} */
	public void setData(JsonObject data){
		this.data = data;
	}
	
	
	/**
	 * Save a data to a json file
	 * @param path The path to save the file to
	 * @param fun A function that accepts the data for a save file, and then saves it, and returns true on success, false otherwise
	 * @return true if the save was successful, false otherwise
	 */
	public static boolean saveJsonFile(String path, Function<JsonObject, Boolean> fun){
		if(path == null) return false;
		try{
			var file = new ZJsonFile(path);
			var data = file.getData();
			var success = fun.apply(data);
			file.setData(data);
			return success && file.save();
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Load data from a json file
	 * @param path The path to load the file from
	 * @param fun A function that accepts the data to  file, and then saves it, and returns true on success, false otherwise
	 * @return true if the save was successful, false otherwise
	 */
	public static boolean loadJsonFile(String path, Function<JsonObject, Boolean> fun){
		if(path == null) return false;
		ZJsonFile file = new ZJsonFile(path);
		JsonObject data = file.load();
		if(data == null) return false;
		try{
			return fun.apply(data);
		}catch(ClassCastException | IllegalStateException | NullPointerException e){
			ZConfig.error(e, "Failed to load a json object because it had invalid formatting. Object data:\n", data);
		}
		return false;
	}
	
}
