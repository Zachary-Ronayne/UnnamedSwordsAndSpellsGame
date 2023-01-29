package zgame.core.graphics.shader;

import static org.lwjgl.opengl.GL30.*;

import zgame.core.utils.ZConfig;
import zgame.core.utils.ZFilePaths;
import zgame.core.utils.ZStringUtils;

/**
 * A class that manages the combination of a vertex and fragment shader for use as a shader program in OpenGL
 */
public class ShaderProgram{
	
	/** The vertex Shader used by this {@link ShaderProgram} */
	private final Shader vertex;
	
	/** The fragment Shader used by this {@link ShaderProgram} */
	private final Shader fragment;
	
	/** The OpenGL program id associated with this {@link ShaderProgram} */
	private int id;
	
	/**
	 * Create a new {@link ShaderProgram}
	 * 
	 * @param vertexPath The file path for {@link #vertex}
	 * @param fragmentPath The file path for {@link #fragment}
	 */
	public ShaderProgram(String vertexPath, String fragmentPath){
		this.vertex = new Shader(vertexPath, GL_VERTEX_SHADER);
		this.fragment = new Shader(fragmentPath, GL_FRAGMENT_SHADER);
		
		this.init();
	}
	
	/**
	 * Create a new ShaderProgram based on the name.
	 * This method assumes that the given name represents two files with the form [name].frag and [name].vert,
	 * where [name] is the given parameter. These files are assumed to be located in {@link ZFilePaths#SHADERS}
	 * 
	 * @param name The name of the shader
	 */
	public ShaderProgram(String name){
		this(ZStringUtils.concat(ZFilePaths.SHADERS, name, ".vert"), ZStringUtils.concat(ZFilePaths.SHADERS, name, ".frag"));
	}
	
	/**
	 * Initialize the state of this {@link ShaderProgram} based on the currently loaded Shaders
	 */
	private void init(){
		this.id = glCreateProgram();
		glAttachShader(this.id, this.getVertex().getId());
		glAttachShader(this.id, this.getFragment().getId());
		glLinkProgram(this.id);
		
		// Error checking
		boolean success = glGetProgrami(this.id, GL_LINK_STATUS) == GL_TRUE;
		String status = success ? "success" : "failure";
		if(ZConfig.printSuccess() && success || ZConfig.printErrors() && !success){
			ZStringUtils.print("Created shader program with vertex shader at\n'", this.vertex.getPath(), "'\n and fragment shader at\n'", this.fragment.getPath(),
					"'\nWith status ", status);
		}
	}
	
	/**
	 * Use this ShaderProgram for OpenGL drawing operations
	 */
	public void use(){
		glUseProgram(this.getId());
	}
	
	/** @return See {@link #vertex} */
	public Shader getVertex(){
		return this.vertex;
	}
	
	/** @return See {@link #fragment} */
	public Shader getFragment(){
		return this.fragment;
	}
	
	/** @return See {@link #id} */
	public int getId(){
		return this.id;
	}
	
}
