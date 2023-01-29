package zgame.core.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import zgame.core.utils.ZConfig;
import zgame.core.utils.ZStringUtils;

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
			if(ZConfig.printErrors()){
				ZStringUtils.prints("Failed to open JSON file at path", this.getPath());
				e.printStackTrace();
			}
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
			if(ZConfig.printErrors()){
				ZStringUtils.prints("Failed to save JSON file to path", this.getPath());
				e.printStackTrace();
			}
			return false;
		}catch(UnsupportedOperationException | NullPointerException | ClassCastException | IllegalStateException e){
			if(ZConfig.printErrors()){
				ZStringUtils.prints("Failed to save JSON file to path", this.getPath(), "with data:", this.getData());
				e.printStackTrace();
			}
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
	
}
