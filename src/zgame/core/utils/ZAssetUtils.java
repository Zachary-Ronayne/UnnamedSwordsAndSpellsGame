package zgame.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.lwjgl.BufferUtils;

/** A class containing utility methods for loading assets */
public final class ZAssetUtils{
	
	/**
	 * Get every file and folder name at a specified directory
	 * 
	 * @param basePath The path to look for names
	 * @param includeExtension true to include the extension on the end of files, false for only the name
	 * @return A list containing every file name. This will contain both files and folders in no guaranteed order
	 */
	public static List<String> getNames(String basePath, boolean includeExtension){
		if(basePath.contains("//")) try{
			throw new Exception();
		}catch(Exception e1){
			e1.printStackTrace();
			System.exit(1);
		}
		basePath = ZStringUtils.concat("/", basePath);
		
		List<String> names = new ArrayList<>();
		FileSystem fileSystem = null;
		Stream<Path> walk = null;
		try{
			// Get a URI which represents the location of the files
			var resource = ZAssetUtils.class.getResource(basePath);
			if(resource == null){
				if(ZConfig.printErrors()) ZStringUtils.print("Failed to load resource, could not find resource. Path: ", basePath);
				return List.of();
			}
			URI uri = resource.toURI();
			Path path;
			
			// If the files are loaded from a jar
			if(uri.getScheme().equals("jar")){
				fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
				path = fileSystem.getPath(basePath);
				
			}
			// If the files are outside the jar
			else path = Paths.get(uri);
			// Iterate through each file
			walk = Files.walk(path, 1);
			
			// Create an iterator to go through every file
			Iterator<Path> it = walk.iterator();
			
			// The first entry is always the directory, this will not be a file
			if(it.hasNext()) it.next();
			
			// Go through all remaining names
			while(it.hasNext()){
				Path nextPath = it.next();
				
				// Find the file name
				String fileName = nextPath.getFileName().toString();
				// If the extension needs to be removed, then remove it
				if(!includeExtension){
					// Find the location of the extension part of the file, then take the appropriate substring
					int dotIndex = fileName.lastIndexOf(".");
					if(dotIndex >= 0) fileName = fileName.substring(0, dotIndex);
				}
				// Add the name
				names.add(fileName);
			}
			if(fileSystem != null) fileSystem.close();
		}catch(URISyntaxException | IOException e){
			if(ZConfig.printErrors()){
				ZStringUtils.prints("Error in ZAssetUtils in loading files");
				e.printStackTrace();
			}
		}finally{
			if(walk != null) walk.close();
		}
		return names;
	}
	
	/**
	 * Get the name of only the files or only the folders contained by the given path.
	 * This method assumes that all files have a file extension
	 * 
	 * @param basePath The path to find the names
	 * @param files true to only include files in the list, false to only include folders
	 * @param extensions true to include file extensions, false otherwise. Only applies if files is true
	 * @return A {@link List} containing the name of every folder
	 */
	public static List<String> getNameTypes(String basePath, boolean files, boolean extensions){
		// Get all files and folders
		List<String> names = getNames(basePath, true);
		List<String> newNames = new ArrayList<>();
		
		// If the file contains a dot, then it is a file with a file extension, otherwise it is a folder
		for(String s : names){
			boolean isFile = s.contains(".");
			if(isFile && files){
				// If needed, remove the file extension
				if(!extensions){
					int dotPos = s.indexOf(".");
					if(dotPos >= 0) s = s.substring(0, dotPos);
				}
				newNames.add(s);
			}
			else if(!isFile && !files) newNames.add(s);
		}
		return newNames;
	}
	
	/**
	 * Get the name of only the files contained by the given path.
	 * This method assumes that all files have a file extension
	 * 
	 * @param basePath The path to find the files
	 * @param extension true to include file extensions, false otherwise.
	 * @return A {@link List} containing the name of every file
	 */
	public static List<String> getAllFiles(String basePath, boolean extension){
		return getNameTypes(basePath, true, extension);
	}
	
	/**
	 * Get the name of only the folders contained by the given path.
	 * This method assumes that all files have a file extension
	 * 
	 * @param basePath The path to find the folders
	 * @return A {@link List} containing the name of every folder
	 */
	public static List<String> getAllFolders(String basePath){
		return getNameTypes(basePath, false, false);
	}
	
	/**
	 * Get an {@link InputStream} which can load files directly from the jar file
	 * 
	 * @param path The path to load from
	 * @return The stream
	 */
	public static InputStream getJarInputStream(String path){
		return ZAssetUtils.class.getClassLoader().getResourceAsStream(path);
	}
	
	/**
	 * Get the bytes of a file directly from the jar file
	 * 
	 * @param path The path to the file
	 * @return A {@link ByteBuffer} containing the data
	 */
	public static ByteBuffer getJarBytes(String path){
		ByteBuffer buff = null;
		InputStream stream;
		try{
			stream = getJarInputStream(path);
			if(stream == null){ throw new IllegalArgumentException("Could not generate an input stream from the jar at location: " +
				path); }
			byte[] bytes = stream.readAllBytes();
			buff = BufferUtils.createByteBuffer(bytes.length);
			buff.put(bytes);
			buff.flip();
			stream.close();
		}catch(IOException e){
			if(ZConfig.printErrors()) ZStringUtils.print("Failed to load '", path, "' from the jar");
		}
		return buff;
	}
	
	/** Cannot instantiate {@link ZAssetUtils} */
	private ZAssetUtils(){
	}
}
