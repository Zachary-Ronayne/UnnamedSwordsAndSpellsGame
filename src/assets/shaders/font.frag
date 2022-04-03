#version 330 core

uniform sampler2D texMap;

in vec4 vTex;

out vec4 fColor;
uniform vec4 mainColor;

void main(){
	vec4 font = texture2D(texMap, vTex.st);
	fColor = vec4(mainColor.rgb, font.a);
}