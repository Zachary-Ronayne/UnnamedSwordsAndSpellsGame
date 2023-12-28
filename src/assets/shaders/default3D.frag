#version 330 core

out vec4 fColor;

uniform vec4 mainColor;
in vec4 vColor;

void main(){
	fColor = vColor;
}