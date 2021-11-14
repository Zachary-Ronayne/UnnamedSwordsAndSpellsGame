uniform sampler2D texMap;
void main(){
	vec4 texColor = texture2D(texMap, gl_TexCoord[0].st);
	gl_FragColor = texColor;
}