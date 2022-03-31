#version 330 core

layout(location = 0) in vec4 color;

out vec4 fontColor;

void main(){
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
	gl_TexCoord[0] = gl_MultiTexCoord0;
	fontColor = color;
}