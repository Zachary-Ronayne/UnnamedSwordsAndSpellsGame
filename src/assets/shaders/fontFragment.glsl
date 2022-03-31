#version 330 core

uniform sampler2D fontMap;
in vec4 fontColor;
void main(){
	vec4 texColor = texture2D(fontMap, gl_TexCoord[0].st);
	gl_FragColor = vec4(fontColor.rgb, texColor.a);
}