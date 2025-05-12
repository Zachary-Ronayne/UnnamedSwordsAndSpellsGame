#version 330 core

uniform sampler2D texMap;

in vec4 vTex;

out vec4 fColor;
uniform vec4 mainColor;

void main(){
	vec4 texVec = texture2D(texMap, vTex.st);
	fColor = vec4(texVec * mainColor);
}