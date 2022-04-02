#version 330 core

uniform sampler2D texMap;

in vec4 vTex;
in vec4 vColor;

out vec4 fColor;

void main(){
	vec4 font = texture2D(texMap, vTex.st);
	fColor = vec4(vColor.rgb, font.a);
}