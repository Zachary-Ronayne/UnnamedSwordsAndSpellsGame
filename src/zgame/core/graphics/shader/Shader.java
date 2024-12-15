package zgame.core.graphics.shader;

import static org.lwjgl.opengl.GL30.*;

import java.io.InputStream;
import java.util.Scanner;

import zgame.core.asset.Asset;
import zgame.core.utils.ZAssetUtils;
import zgame.core.utils.ZConfig;

/**
 * A class that handles an individual shader, i.e. a vertex or fragment shader for OpenGL.
 * Mostly a helper class for {@link ShaderProgram}
 */
public class Shader extends Asset{
	
	/** The code that runs the shader */
	private String code;
	
	/** The OpenGL id associated with this shader */
	private int id;
	
	/** The type of shader, i.e. either GL_VERTEX_SHADER or GL_FRAGMENT_SHADER */
	private final int type;
	
	public Shader(String path, int type){
		super(path);
		this.type = type;
		this.load();
		this.init();
	}
	
	/**
	 * Load the shader at the file path of this asset, i.e. take the code from that file and use it for this shader.
	 * This method does not update the shader itself, it only loads the shader from the file
	 */
	public void load(){
		String path = this.getPath();
		Scanner file = null;
		try{
			// Get the file from the jar
			InputStream stream = ZAssetUtils.getJarInputStream(path);
			
			// Open the file
			file = new Scanner(stream);
			
			// Read the entire file and put it into the code variable
			StringBuilder sb = new StringBuilder();
			while(file.hasNextLine()){
				sb.append(file.nextLine());
				sb.append("\n");
			}
			this.code = sb.toString();
			ZConfig.success("Successfully loaded shader at '", path, "'");
		}catch(Exception e){
			// Error checking
			ZConfig.error(e);
		}finally{
			// Ensure the file closes
			if(file != null) file.close();
		}
	}
	
	/**
	 * Set up the shader based on the current code
	 */
	private void init(){
		this.id = glCreateShader(this.getType());
		glShaderSource(this.id, this.code);
		glCompileShader(this.id);
		
		// Error check
		boolean success = glGetShaderi(this.id, GL_COMPILE_STATUS) == GL_TRUE;
		if(success) ZConfig.success("Shader at path '", this.getPath(), "' initialized");
		else ZConfig.error("Shader at path '", this.getPath(), "' initialize failure");
	}
	
	@Override
	public void destroy(){
	}
	
	/** @return See {@link #id} */
	public int getId(){
		return this.id;
	}
	
	/** @return See {@link #type} */
	public int getType(){
		return this.type;
	}
	
}
