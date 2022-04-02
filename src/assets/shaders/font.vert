#version 330 core

layout(location = 0) in vec4 pos;
layout(location = 1) in vec4 color;
layout(location = 2) in vec4 tex;

out vec4 vTex;
out vec4 vColor;

uniform mat4 modelView;

void main(){
	gl_Position = modelView * pos;
	vTex = tex;
	vColor = color;
}