#version 330 core

layout(location = 0) in vec4 pos;

out vec4 vColor;

uniform mat4 modelView;

void main(){
	gl_Position = modelView * pos;
}