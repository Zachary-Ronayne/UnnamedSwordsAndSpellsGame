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
public final class AssetUtils{
	
	/**
	 * Get every file name at a specified directory
	 * 
	 * @param basePath The path to look for files
	 * @param includeExtension true to include the extension on the end of files, false for only the name
	 * @return A list containing every file name
	 */
	public static List<String> getFileNames(String basePath, boolean includeExtension){
		List<String> names = new ArrayList<String>();
		FileSystem fileSystem = null;
		Stream<Path> walk = null;
		try{
			// Get a URI which represents the location of the files
			URI uri = AssetUtils.class.getResource(basePath).toURI();
			Path path;
			
			// If the files are loaded from a jar
			if(uri.getScheme().equals("jar")){
				fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
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
				ZStringUtils.prints("Error in AssetUtils in loading files");
				e.printStackTrace();
			}
		}finally{
			if(walk != null) walk.close();
		}
		return names;
	}
	
	/**
	 * Get an {@link InputStream} which can load files directly from the jar file
	 * 
	 * @param path The path to load from
	 * @return The stream
	 */
	public static InputStream getJarInputStream(String path){
		return AssetUtils.class.getClassLoader().getResourceAsStream(path);
	}
	
	/**
	 * Get the bytes of a file directly from the jar file
	 * 
	 * @param path The path to the file
	 * @return A {@link ByteBuffer} containing the data
	 */
	public static ByteBuffer getJarBytes(String path){
		ByteBuffer buff = null;
		InputStream stream = null;
		try{
			stream = getJarInputStream(path);
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
	
	/** Cannot instantiate {@link #AssetUtils()} */
	private AssetUtils(){
	}
}
