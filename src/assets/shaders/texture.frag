#version 330 core

uniform sampler2D texMap;

in vec4 vTex;

out vec4 fColor;

void main(){
	fColor = texture2D(texMap, vTex.st);
}